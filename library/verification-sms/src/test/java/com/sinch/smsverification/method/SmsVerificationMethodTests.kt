package com.sinch.smsverification.method

import android.app.Application
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.google.android.gms.common.api.Status
import com.sinch.smsverification.*
import com.sinch.smsverification.Constants.apiSmsTimeout
import com.sinch.smsverification.SmsTemplates.CODE
import com.sinch.smsverification.SmsTemplates.exampleSimple1
import com.sinch.smsverification.config.SmsVerificationConfig
import com.sinch.smsverification.initialization.SmsInitializationDetails
import com.sinch.smsverification.initialization.SmsInitializationListener
import com.sinch.smsverification.initialization.SmsInitiationResponseData
import com.sinch.verificationcore.config.general.GlobalConfig
import com.sinch.verificationcore.internal.Verification
import com.sinch.verificationcore.internal.VerificationState
import com.sinch.verificationcore.internal.VerificationStateStatus
import com.sinch.verificationcore.internal.VerificationStatus
import com.sinch.verificationcore.internal.error.CodeInterceptionException
import com.sinch.verificationcore.verification.response.VerificationListener
import com.sinch.verificationcore.verification.response.VerificationResponseData
import io.mockk.*
import io.mockk.impl.annotations.MockK
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import retrofit2.mock.Calls
import java.util.concurrent.TimeUnit

@RunWith(
    RobolectricTestRunner::class
)
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
            VerificationState.Initialization(VerificationStateStatus.ONGOING),
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
        Assert.assertEquals(
            VerificationState.Initialization(VerificationStateStatus.SUCCESS),
            basicSmsMethod.verificationState
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
            VerificationState.Initialization(VerificationStateStatus.ERROR),
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
    fun testUserDefinedTimeout() {
        val verification = prepareVerification()
        prepareMocks()
        verification.initiate()
        Robolectric.getForegroundThreadScheduler().advanceBy(apiSmsTimeout / 2, TimeUnit.SECONDS)
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

        every { mockedService.verifyNumber(any(), any()) }.returns(
            Calls.failure(mockk())
        )
        every {
            mockedService.verifyNumber(any(), match {
                it.details.code == VERIFICATION_CODE
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