package com.sinch.smsverification.method

import android.app.Application
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.sinch.smsverification.SmsVerificationMethod
import com.sinch.smsverification.SmsVerificationService
import com.sinch.smsverification.config.SmsVerificationConfig
import com.sinch.smsverification.initialization.SmsInitializationListener
import com.sinch.smsverification.initialization.SmsInitiationResponseData
import com.sinch.verificationcore.config.general.GlobalConfig
import com.sinch.verificationcore.internal.VerificationStateStatus
import com.sinch.verificationcore.internal.error.VerificationState
import com.sinch.verificationcore.verification.response.VerificationListener
import io.mockk.*
import io.mockk.impl.annotations.MockK
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import retrofit2.mock.Calls

@RunWith(
    RobolectricTestRunner::class
)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class SmsVerificationMethodTests {

    private val appContext = ApplicationProvider.getApplicationContext<Application>()

    private val mockedService = mockk<SmsVerificationService>(relaxed = true)

    private val mockedGlobalConfig = spyk<GlobalConfig> {
        every { context } returns (appContext)
        every { retrofit } returns mockk() {
            every { create(SmsVerificationService::class.java) } returns mockedService
        }
    }

    @MockK
    lateinit var mockedInitListener: SmsInitializationListener

    @MockK
    lateinit var mockedVerificationListener: VerificationListener

    private val basicConfig: SmsVerificationConfig
        get() = SmsVerificationConfig(
            mockedGlobalConfig,
            "+48123456789"
        )

    private val basicSmsMethod: SmsVerificationMethod by lazy {
        SmsVerificationMethod(
            basicConfig,
            mockedInitListener,
            mockedVerificationListener
        )
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
        verify { mockedInitListener.onInitiated(mockedResponse, any()) }
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
}