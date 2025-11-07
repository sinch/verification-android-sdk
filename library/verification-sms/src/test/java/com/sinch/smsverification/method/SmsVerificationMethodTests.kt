package com.sinch.smsverification.method

import android.app.Application
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.google.android.gms.common.api.Status
import com.sinch.smsverification.Constants
import com.sinch.smsverification.Constants.apiSmsTimeout
import com.sinch.smsverification.SmsBroadcastReceiverTests
import com.sinch.smsverification.SmsTemplates.CODE
import com.sinch.smsverification.SmsTemplates.exampleSimple1
import com.sinch.smsverification.VerCodes
import com.sinch.verification.core.config.general.GlobalConfig
import com.sinch.verification.core.internal.Verification
import com.sinch.verification.core.internal.VerificationState
import com.sinch.verification.core.internal.VerificationStateStatus
import com.sinch.verification.core.internal.VerificationStatus
import com.sinch.verification.core.internal.error.CodeInterceptionException
import com.sinch.verification.core.verification.response.VerificationListener
import com.sinch.verification.core.verification.response.VerificationResponseData
import com.sinch.verification.sms.SmsVerificationMethod
import com.sinch.verification.sms.SmsVerificationService
import com.sinch.verification.sms.config.SmsVerificationConfig
import com.sinch.verification.sms.initialization.SmsInitializationDetails
import com.sinch.verification.sms.initialization.SmsInitializationListener
import com.sinch.verification.sms.initialization.SmsInitiationResponseData
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import java.util.concurrent.TimeUnit
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode
import retrofit2.mock.Calls

@RunWith(
    RobolectricTestRunner::class
)
@LooperMode(LooperMode.Mode.LEGACY)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class SmsVerificationMethodTests {

    companion object {
        const val VERIFICATION_CODE = VerCodes.simple1
    }

    private val appContext = ApplicationProvider.getApplicationContext<Application>()

    private val mockedService = mockk<SmsVerificationService>(relaxed = true)

    private val mockedGlobalConfig = spyk<GlobalConfig> {
        every { context } returns (appContext)
        every { retrofit } returns mockk {
            every { create(SmsVerificationService::class.java) } returns mockedService
        }
    }

    @MockK
    lateinit var mockedInitListener: SmsInitializationListener

    @MockK
    lateinit var mockedVerificationListener: VerificationListener

    private val basicSmsMethod: Verification by lazy {
        prepareVerification()
    }

    @Before
    fun setupUp() {
        MockKAnnotations.init(this, relaxed = true)
    }

    @Test
    fun testInitialState() {
        Assert.assertEquals(basicSmsMethod.verificationState, VerificationState.IDLE)
    }

    @Test
    fun testStateAfterInitialization() {
        basicSmsMethod.initiate()
        Assert.assertEquals(
            VerificationState.Initialization(VerificationStateStatus.ONGOING, null),
            basicSmsMethod.verificationState
        )
    }

    @Test
    fun testSuccessfulInitialization() {
        val mockedResponse = mockk<SmsInitiationResponseData>(relaxed = true)
        every { mockedService.initializeVerification(any(), any()) }.returns(
            Calls.response(mockedResponse)
        )
        basicSmsMethod.initiate()
        Assert.assertTrue(
            basicSmsMethod.verificationState is VerificationState.Initialization &&
                (basicSmsMethod.verificationState as VerificationState.Initialization).status == VerificationStateStatus.SUCCESS
        )
        verify { mockedInitListener.onInitiated(any()) }
    }

    @Test
    fun testErrorInitialization() {
        val error = mockk<Throwable>()
        every { mockedService.initializeVerification(any(), any()) }.returns(
            Calls.failure(error)
        )
        basicSmsMethod.initiate()
        Assert.assertEquals(
            VerificationState.Initialization(VerificationStateStatus.ERROR, null),
            basicSmsMethod.verificationState
        )
        verify { mockedInitListener.onInitializationFailed(error) }
    }

    @Test
    fun testCorrectManualFlow() {
        prepareMocks()
        basicSmsMethod.initiate()
        basicSmsMethod.verify(VERIFICATION_CODE)

        verify(exactly = 0) { mockedVerificationListener.onVerificationFailed(any()) }
        verify(exactly = 1) { mockedVerificationListener.onVerified() }
    }

    @Test
    fun testWrongCodeNotifications() {
        prepareMocks()
        basicSmsMethod.initiate()
        basicSmsMethod.verify("${VERIFICATION_CODE}WRONG")

        verify(exactly = 1) { mockedVerificationListener.onVerificationFailed(any()) }
        verify(exactly = 0) { mockedVerificationListener.onVerified() }
    }

    @Test
    fun testMultipleVerificationNotifyListenerOnce() {
        prepareMocks()
        basicSmsMethod.initiate()
        for (i in 0..10) {
            basicSmsMethod.verify(VERIFICATION_CODE)
        }

        verify(exactly = 0) { mockedVerificationListener.onVerificationFailed(any()) }
        verify(exactly = 1) { mockedVerificationListener.onVerified() }
    }

    @Test
    fun testAutomaticVerificationSuccess() {
        prepareMocks()
        basicSmsMethod.initiate()
        val mockedBroadcastIntent = SmsBroadcastReceiverTests.mockedBroadcastIntent(
            exampleSimple1.replace(
                CODE,
                VERIFICATION_CODE
            ), Status.RESULT_SUCCESS
        )
        appContext.sendBroadcast(mockedBroadcastIntent)

        verify(exactly = 0) { mockedVerificationListener.onVerificationFailed(any()) }
        verify(exactly = 1) { mockedVerificationListener.onVerified() }
    }

    @Test
    fun testAutomaticFailureManualSuccess() {
        prepareMocks()
        basicSmsMethod.initiate()
        val mockedBroadcastIntent = SmsBroadcastReceiverTests.mockedBroadcastIntent(
            exampleSimple1.replace(
                CODE,
                VERIFICATION_CODE
            ), Status.RESULT_TIMEOUT
        )
        appContext.sendBroadcast(mockedBroadcastIntent)
        verify(exactly = 1) { mockedVerificationListener.onVerificationFailed(any()) }
        verify(exactly = 0) { mockedVerificationListener.onVerified() }

        basicSmsMethod.verify(VERIFICATION_CODE)
        verify(exactly = 1) { mockedVerificationListener.onVerified() }
    }

    @Test
    fun testFailureStatusNotifiesCorrectListener() {
        prepareMocks(returnedStatus = VerificationStatus.FAILED)
        basicSmsMethod.initiate()
        basicSmsMethod.verify(VERIFICATION_CODE)
        verify(exactly = 1) { mockedVerificationListener.onVerificationFailed(any()) }
        verify(exactly = 0) { mockedVerificationListener.onVerified() }
    }

    @Test
    fun testApiTimeout() {
        prepareMocks()
        basicSmsMethod.initiate()
        Robolectric.getForegroundThreadScheduler().advanceBy(apiSmsTimeout * 2, TimeUnit.SECONDS)
        verify(exactly = 1) { mockedVerificationListener.onVerificationFailed(any<CodeInterceptionException>()) }
        verify(exactly = 0) { mockedVerificationListener.onVerified() }
    }

    @Test
    fun testApiTimeoutUsedInsteadOfUser() {
        val verification = prepareVerification()
        prepareMocks()
        verification.initiate()
        Robolectric.getForegroundThreadScheduler().advanceBy(apiSmsTimeout + 1, TimeUnit.SECONDS)
        verify(exactly = 1) { mockedVerificationListener.onVerificationFailed(any<CodeInterceptionException>()) }
        verify(exactly = 0) { mockedVerificationListener.onVerified() }
    }

    @Test
    fun testNoNotificationsAfterStopCalled() {
        prepareMocks()
        basicSmsMethod.apply {
            initiate()
            stop()
        }
        val mockedBroadcastIntent = SmsBroadcastReceiverTests.mockedBroadcastIntent(
            exampleSimple1.replace(
                CODE,
                VERIFICATION_CODE
            ), Status.RESULT_SUCCESS
        )
        appContext.sendBroadcast(mockedBroadcastIntent)
        verify(exactly = 0) { mockedVerificationListener.onVerificationFailed(any()) }
        verify(exactly = 0) { mockedVerificationListener.onVerified() }
    }

    private fun prepareMocks(returnedStatus: VerificationStatus = VerificationStatus.SUCCESSFUL) {
        val mockedInitResponse =
            SmsInitiationResponseData("", SmsInitializationDetails(exampleSimple1, apiSmsTimeout))

        val mockedVerResponse = mockk<VerificationResponseData>(relaxed = true) {
            every { status } returns returnedStatus
        }

        every { mockedService.initializeVerification(any(), any()) }.returns(
            Calls.response(mockedInitResponse)
        )

        every { mockedService.verifyById(any(), any()) }.returns(
            Calls.failure(mockk())
        )
        every {
            mockedService.verifyById(any(), match {
                it.smsDetails?.code == VERIFICATION_CODE
            })
        }.returns(
            Calls.response(mockedVerResponse)
        )
    }

    private fun prepareVerification() =
        SmsVerificationMethod.Builder.instance
            .config(
                SmsVerificationConfig.Builder.instance
                    .globalConfig(mockedGlobalConfig)
                    .number(Constants.phone)
                    .appHash(Constants.appHash)
                    .build()
            )
            .initializationListener(mockedInitListener)
            .verificationListener(mockedVerificationListener)
            .build()

}