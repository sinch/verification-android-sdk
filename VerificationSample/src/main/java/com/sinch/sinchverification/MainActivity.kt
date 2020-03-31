package com.sinch.sinchverification

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.facebook.flipper.plugins.network.FlipperOkhttpInterceptor
import com.sinch.logging.logger
import com.sinch.smsverification.SmsVerificationMethod
import com.sinch.smsverification.config.SmsVerificationConfig
import com.sinch.smsverification.initialization.SmsInitializationListener
import com.sinch.smsverification.initialization.SmsInitiationResponseData
import com.sinch.verificationcore.auth.AppKeyAuthorizationMethod
import com.sinch.verificationcore.config.general.SinchGlobalConfig
import com.sinch.verificationcore.internal.Verification
import com.sinch.verificationcore.verification.response.VerificationListener
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.Interceptor

class MainActivity : AppCompatActivity() {

    private val app: VerificationSampleApp get() = application as VerificationSampleApp
    private val logger = logger()

    private val globalConfig by lazy {
        SinchGlobalConfig.Builder(app)
            .apiHost("https://verificationapi-v1.sinch.com/")
            //.apiHost("https://verificationapi-v1-01.sinchlab.com/")
            .interceptors(listOf<Interceptor>(FlipperOkhttpInterceptor(app.networkPlugin)))
            //  .authMethod(AppKeyAuthorizationMethod("de23e021-db44-4004-902c-5a7fc18e35e2")).build()
            .authMethod(AppKeyAuthorizationMethod("9e556452-e462-4006-aab0-8165ca04de66")).build()
    }

    private val testListener = object : SmsInitializationListener {
        override fun onInitiated(
            data: SmsInitiationResponseData,
            contentLanguage: String
        ) {
            logger.debug("Test app onInitiated")
        }

        override fun onInitializationFailed(t: Throwable) {
            logger.debug("Test app onInitializationFailed")
        }
    }

    private val testListenerVerification = object : VerificationListener {
        override fun onVerified() {
            logger.debug("Test app onVerified")
        }

        override fun onVerificationFailed(t: Throwable) {
            logger.debug("Test app onVerificationFailed")
        }
    }

    private var verification: Verification? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initButton.setOnClickListener {
            verification?.initiate()
        }
        verifyButton.setOnClickListener {
            verification?.verify(codeEdit.text.toString())
        }
        phoneNumber.addTextChangedListener {
            resetVerification()
        }
        resetVerification()
    }

    private fun resetVerification() {
        verification = SmsVerificationMethod(
            SmsVerificationConfig(
                config = globalConfig,
                number = phoneNumber.text.toString(),
                acceptedLanguages = listOf("es-ES"),
                honourEarlyReject = true,
                appHash = "0wjBaTjBink",
                custom = "testCustom",
                maxTimeout = null
            ),
            initializationListener = testListener,
            verificationListener = testListenerVerification
        )
    }

}
