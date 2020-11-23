package com.sinch.verification.callout

import android.app.Application
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.sinch.verification.callout.config.CalloutVerificationConfig
import com.sinch.verification.callout.initialization.CalloutInitializationListener
import com.sinch.verification.callout.initialization.CalloutInitializationResponseData
import com.sinch.verification.callout.verification.CalloutVerificationData
import com.sinch.verification.utils.MAX_TIMEOUT
import com.sinch.verification.utils.permission.Permission
import com.sinch.verification.core.config.general.GlobalConfig
import com.sinch.verification.core.internal.VerificationMethodType
import com.sinch.verification.core.internal.VerificationStatus
import com.sinch.verification.core.verification.interceptor.CodeInterceptionTimeoutException
import com.sinch.verification.core.verification.response.VerificationListener
import com.sinch.verification.core.verification.response.VerificationResponseData
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
class CalloutVerificationMethodTests {

    companion object {
        const val SUCCESS_CODE = "1234"
    }

    private val appContext = ApplicationProvider.getApplicationContext<Application>()

    private val mockedService = mockk<CalloutVerificationService>(relaxed = true)

    private val mockedGlobalConfig = spyk<GlobalConfig> {
        every { context } returns (appContext)
        every { retrofit } returns mockk {
            every { create(CalloutVerificationService::class.java) } returns mockedService
        }
    }

    private val calloutVerification by lazy {
        prepareVerification()
    }

    @MockK
    lateinit var mockedInitListener: CalloutInitializationListener

    @MockK
    lateinit var mockedVerificationListener: VerificationListener

    @Before
    fun setupUp() {
        MockKAnnotations.init(this, relaxed = true)
        Shadows.shadowOf(appContext).grantPermissions(Permission.READ_CALL_LOG.androidValue)
    }

    @Test
    fun testBasicVerificationFlow() {
        setupCorrectInitResponse()
        setupDefaultVerificationResponse()
        calloutVerification.apply {
            initiate()
            verify(SUCCESS_CODE)
        }
        verifySequence {
            mockedInitListener.onInitiated(any())
            mockedVerificationListener.onVerified()
        }
    }

    @Test
    fun testWrongCodePassedFlow() {
        setupCorrectInitResponse()
        setupDefaultVerificationResponse()
        calloutVerification.apply {
            initiate()
            verify("$SUCCESS_CODE 1")
        }
        verifySequence {
            mockedInitListener.onInitiated(any())
            mockedVerificationListener.onVerificationFailed(any())
        }
    }

    @Test
    fun testTimeoutExceptionThrown() {
        setupCorrectInitResponse()
        setupDefaultVerificationResponse()
        calloutVerification.apply {
            initiate()
        }
        Robolectric.getForegroundThreadScheduler()
            .advanceBy(Long.MAX_TIMEOUT + 10, TimeUnit.MILLISECONDS)
        verifySequence {
            mockedInitListener.onInitiated(any())
            mockedVerificationListener.onVerificationFailed(any<CodeInterceptionTimeoutException>())
        }

    }

    private fun prepareVerification() =
        CalloutVerificationMethod.Builder.instance
            .config(
                CalloutVerificationConfig.Builder.instance
                    .globalConfig(mockedGlobalConfig)
                    .number("")
                    .build()
            )
            .initializationListener(mockedInitListener)
            .verificationListener(mockedVerificationListener)
            .build()

    private fun setupCorrectInitResponse() {
        val mockedInitResponse = CalloutInitializationResponseData(
            id = ""
        )
        every { mockedService.initializeVerification(any()) } returns Calls.response(
            mockedInitResponse
        )
    }

    private fun setupDefaultVerificationResponse() {
        every { mockedService.verifyNumber(any(), any()) } answers {
            if (secondArg<CalloutVerificationData>().details.code == SUCCESS_CODE) {
                Calls.response(
                    VerificationResponseData(
                        id = "",
                        status = VerificationStatus.SUCCESSFUL,
                        method = VerificationMethodType.CALLOUT
                    )
                )
            } else {
                Calls.failure(Exception())
            }

        }
    }

}
