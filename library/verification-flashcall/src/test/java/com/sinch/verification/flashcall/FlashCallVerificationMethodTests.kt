package com.sinch.verification.flashcall

import android.app.Application
import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager
import androidx.test.core.app.ApplicationProvider
import com.sinch.utils.permission.Permission
import com.sinch.verification.flashcall.config.FlashCallVerificationConfig
import com.sinch.verification.flashcall.initialization.FlashCallInitializationDetails
import com.sinch.verification.flashcall.initialization.FlashCallInitializationListener
import com.sinch.verification.flashcall.initialization.FlashCallInitializationResponseData
import com.sinch.verification.flashcall.report.FlashCallReportData
import com.sinch.verification.flashcall.report.FlashCallReportDetails
import com.sinch.verificationcore.config.general.GlobalConfig
import com.sinch.verificationcore.internal.VerificationStatus
import com.sinch.verificationcore.internal.error.CodeInterceptionException
import com.sinch.verificationcore.internal.error.VerificationException
import com.sinch.verificationcore.verification.response.VerificationListener
import com.sinch.verificationcore.verification.response.VerificationResponseData
import io.mockk.*
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import retrofit2.mock.Calls
import java.util.concurrent.TimeUnit

@RunWith(
    RobolectricTestRunner::class
)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class FlashCallVerificationMethodTests {

    private val appContext = ApplicationProvider.getApplicationContext<Application>()

    private val telephonyManager =
        Shadows.shadowOf(appContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager)

    private val mockedService = mockk<FlashCallVerificationService>(relaxed = true)

    private val mockedGlobalConfig = spyk<GlobalConfig> {
        every { context } returns (appContext)
        every { retrofit } returns mockk {
            every { create(FlashCallVerificationService::class.java) } returns mockedService
        }
    }

    @MockK
    lateinit var mockedInitListener: FlashCallInitializationListener

    @MockK
    lateinit var mockedVerificationListener: VerificationListener

    @Before
    fun setupUp() {
        MockKAnnotations.init(this, relaxed = true)
        Shadows.shadowOf(appContext).grantPermissions(Permission.READ_CALL_LOG.androidValue)
    }

    @Test
    fun testListenerNotifiedAboutFailureWhenPermissionsMissing() {
        Shadows.shadowOf(appContext).denyPermissions(Permission.READ_CALL_LOG.androidValue)
        val verification = prepareVerification()
        verification.initiate()
        verify(exactly = 1) { mockedInitListener.onInitializationFailed(any<VerificationException>()) }
    }

    @Test
    fun testBasicVerificationFlow() {
        setupCorrectInitResponse()
        setupDefaultVerificationResponse()
        prepareVerification().apply {
            initiate()
        }
        verifySequence { mockedInitListener.onInitiated(any()) }
        telephonyManager.setCallState(
            TelephonyManager.CALL_STATE_RINGING,
            Constants.phoneMatchingTemplate1
        )
        verify {
            mockedService.verifyNumber(Constants.phone, match {
                it.details.cli == Constants.phoneMatchingTemplate1
            })
        }
        verifySequence { mockedVerificationListener.onVerified() }
        testReportWasSent(isLateCall = false, isNoCall = false)
    }

    @Test
    fun testNoPhoneMatchingTemplateFlow() {
        setupCorrectInitResponse()
        setupDefaultVerificationResponse()
        prepareVerification().apply {
            initiate()
        }
        verifySequence { mockedInitListener.onInitiated(any()) }
        telephonyManager.setCallState(
            TelephonyManager.CALL_STATE_RINGING,
            Constants.phoneNonMatchingTemplate1
        )
        verify(exactly = 0) { mockedService.verifyNumber(any(), any()) }
        Robolectric.getForegroundThreadScheduler()
            .advanceBy(ApiTimeouts.interceptionTimeout + 1, TimeUnit.SECONDS)
        verify(exactly = 0) { mockedService.verifyNumber(any(), any()) }
        verify(exactly = 0) { mockedService.reportVerification(any(), any()) }
        verify { mockedVerificationListener.onVerificationFailed(any<CodeInterceptionException>()) }

        Robolectric.getForegroundThreadScheduler()
            .advanceBy(
                (ApiTimeouts.reportTimeout - ApiTimeouts.interceptionTimeout),
                TimeUnit.SECONDS
            )

        testReportWasSent(isLateCall = false, isNoCall = true)
    }

    @Test
    fun testLateCallFlow() {
        setupCorrectInitResponse()
        setupDefaultVerificationResponse()
        prepareVerification().apply {
            initiate()
        }

        Robolectric.getForegroundThreadScheduler()
            .advanceBy(ApiTimeouts.interceptionTimeout + 1, TimeUnit.SECONDS)
        verify(exactly = 0) { mockedService.verifyNumber(any(), any()) }
        verify(exactly = 0) { mockedService.reportVerification(any(), any()) }
        verify { mockedVerificationListener.onVerificationFailed(any<CodeInterceptionException>()) }

        telephonyManager.setCallState(
            TelephonyManager.CALL_STATE_RINGING,
            Constants.phoneMatchingTemplate1
        )

        verify(exactly = 0) { mockedVerificationListener.onVerified() }
        testReportWasSent(isLateCall = true, isNoCall = false)
    }

    @Test
    fun testNoReportSentAfterStopped() {
        setupCorrectInitResponse()
        setupDefaultVerificationResponse()
        prepareVerification().apply {
            initiate()
            stop()
        }
        telephonyManager.setCallState(
            TelephonyManager.CALL_STATE_RINGING,
            Constants.phoneMatchingTemplate1
        )
        verify(exactly = 0) { mockedVerificationListener.onVerified() }
        verify(exactly = 0) { mockedService.reportVerification(any(), any()) }
    }

    private fun testReportWasSent(isLateCall: Boolean, isNoCall: Boolean) {
        verify {
            mockedService.reportVerification(
                Constants.phone, FlashCallReportData(
                    details = FlashCallReportDetails(
                        isLateCall = isLateCall,
                        isNoCall = isNoCall
                    )
                )
            )
        }
    }

    private fun setupCorrectInitResponse(
        interceptionTimeout: Long = ApiTimeouts.interceptionTimeout,
        reportTimeout: Long = ApiTimeouts.reportTimeout
    ) {
        val mockedInitResponse = FlashCallInitializationResponseData(
            id = "",
            details = FlashCallInitializationDetails(
                cliFilter = CliTemplates.template1,
                interceptionTimeoutApi = interceptionTimeout,
                reportTimeoutApi = reportTimeout,
                denyCallAfter = 0
            )
        )
        every { mockedService.initializeVerification(any()) } returns Calls.response(
            mockedInitResponse
        )
    }

    private fun setupDefaultVerificationResponse(usedCode: String = Constants.phoneMatchingTemplate1) {
        val verificationResponseData = mockk<VerificationResponseData>(relaxed = true) {
            every { status } returns VerificationStatus.SUCCESSFUL
        }

        every { mockedService.verifyNumber(any(), any()) }.returns(
            Calls.failure(mockk())
        )
        every {
            mockedService.verifyNumber(any(), match {
                it.details.cli == usedCode
            })
        }.returns(
            Calls.response(verificationResponseData)
        )
    }

    private fun prepareVerification(userTimeout: Long? = null) =
        FlashCallVerificationMethod.Builder.instance
            .config(
                FlashCallVerificationConfig.Builder.instance
                    .globalConfig(mockedGlobalConfig)
                    .number(Constants.phone)
                    .maxTimeout(userTimeout)
                    .build()
            )
            .initializationListener(mockedInitListener)
            .verificationListener(mockedVerificationListener)
            .build()

}