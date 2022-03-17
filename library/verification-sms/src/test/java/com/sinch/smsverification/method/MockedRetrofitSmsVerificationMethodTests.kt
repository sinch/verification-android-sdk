package com.sinch.smsverification.method

import android.app.Application
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.google.android.gms.common.api.Status
import com.sinch.smsverification.Constants
import com.sinch.smsverification.SmsBroadcastReceiverTests
import com.sinch.smsverification.SmsTemplates
import com.sinch.verification.core.auth.AppKeyAuthorizationMethod
import com.sinch.verification.core.config.general.SinchGlobalConfig
import com.sinch.verification.core.internal.VerificationMethodType
import com.sinch.verification.core.internal.VerificationStatus
import com.sinch.verification.core.verification.response.VerificationListener
import com.sinch.verification.core.verification.response.VerificationResponseData
import com.sinch.verification.sms.SmsVerificationMethod
import com.sinch.verification.sms.SmsVerificationService
import com.sinch.verification.sms.config.SmsVerificationConfig
import com.sinch.verification.sms.initialization.SmsInitializationDetails
import com.sinch.verification.sms.initialization.SmsInitializationListener
import com.sinch.verification.sms.initialization.SmsInitiationResponseData
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.util.concurrent.RoboExecutorService
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode
import retrofit2.mock.Calls
import retrofit2.mock.MockRetrofit
import retrofit2.mock.NetworkBehavior
import java.util.concurrent.TimeUnit

@RunWith(
    RobolectricTestRunner::class
)
@LooperMode(LooperMode.Mode.LEGACY)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class MockedRetrofitSmsVerificationMethodTests {

    private val appContext = ApplicationProvider.getApplicationContext<Application>()

    @MockK
    lateinit var mockedInitListener: SmsInitializationListener

    @MockK
    lateinit var mockedVerificationListener: VerificationListener

    private val globalConfig by lazy {
        SinchGlobalConfig.Builder.instance.applicationContext(appContext)
            .authorizationMethod(AppKeyAuthorizationMethod(""))
            .apiHost("https://localhost.com/").build()
    }

    private val mockedBroadcastIntent by lazy {
        SmsBroadcastReceiverTests.mockedBroadcastIntent(
            SmsTemplates.exampleSimple1.replace(
                SmsTemplates.CODE,
                SmsVerificationMethodTests.VERIFICATION_CODE
            ), Status.RESULT_SUCCESS
        )
    }

    private val mockedRetrofit by lazy {
        MockRetrofit.Builder(globalConfig.retrofit).networkBehavior(
            NetworkBehavior.create().apply {
                setDelay(1, TimeUnit.SECONDS)
                setErrorPercent(0)
                setFailurePercent(0)
            }
        ).backgroundExecutor(executor).build()
    }

    private val executor by lazy {
        RoboExecutorService()
    }

    @Before
    fun setupUp() {
        MockKAnnotations.init(this, relaxed = true)
    }

    @Test
    fun testVerificationSuccessWhenSmsReceivedBeforeApiResponse() {
        val mockedService =
            MockedSmsVerificationService(mockedRetrofit.create(SmsVerificationService::class.java)).apply {
                initializeVerificationCallCallback = { _, _ ->
                    Calls.response(
                        SmsInitiationResponseData(
                            id = "",
                            details = SmsInitializationDetails(
                                SmsTemplates.exampleSimple1,
                                Constants.apiSmsTimeout
                            )
                        )
                    )
                }
                verifyCallCallback = { _, _ ->
                    Calls.response(
                        VerificationResponseData(
                            id = "",
                            source = null,
                            status = VerificationStatus.SUCCESSFUL,
                            errorReason = null,
                            method = VerificationMethodType.SMS
                        )
                    )
                }
            }
        Robolectric.getBackgroundThreadScheduler().pause()
        SmsVerificationMethod.Builder.instance.config(prepareConfigWithService(mockedService))
            .initializationListener(mockedInitListener)
            .verificationListener(mockedVerificationListener)
            .build()
            .initiate()
        appContext.sendBroadcast(mockedBroadcastIntent)

        verify(exactly = 0) { mockedInitListener.onInitiated(any()) }

        Robolectric.getBackgroundThreadScheduler().advanceBy(2, TimeUnit.SECONDS)
        verify(exactly = 1) { mockedInitListener.onInitiated(any()) }
        verify(exactly = 1) { mockedVerificationListener.onVerified() }
    }

    private fun prepareConfigWithService(smsVerificationService: SmsVerificationService): SmsVerificationConfig =
        SmsVerificationConfig(
            globalConfig = globalConfig,
            number = Constants.phone,
            appHash = Constants.appHash,
            apiService = smsVerificationService
        )
}