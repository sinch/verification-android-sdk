package com.sinch.sinchverification

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.sinch.logging.logger
import com.sinch.smsverification.SmsVerificationMethod
import com.sinch.smsverification.config.SmsVerificationConfig
import com.sinch.smsverification.initialization.SmsInitializationListener
import com.sinch.smsverification.initialization.SmsInitiationResponseData
import com.sinch.verification.flashcall.initialization.FlashCallInitializationListener
import com.sinch.verification.flashcall.initialization.FlashCallInitializationResponseData
import com.sinch.verificationcore.auth.AppKeyAuthorizationMethod
import com.sinch.verificationcore.config.general.SinchGlobalConfig
import com.sinch.verificationcore.internal.Verification
import com.sinch.verificationcore.verification.response.VerificationListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val app: VerificationSampleApp get() = application as VerificationSampleApp
    private val logger = logger()

    private val globalConfig by lazy {
        SinchGlobalConfig.Builder.instance.applicationContext(app)
            //.authorizationMethod(AppKeyAuthorizationMethod("de23e021-db44-4004-902c-5a7fc18e35e2"))
            .authorizationMethod(AppKeyAuthorizationMethod("9e556452-e462-4006-aab0-8165ca04de66"))
            .apiHost("https://verificationapi-v1.sinch.com/")
            //.apiHost("https://verificationapi-v1-01.sinchlab.com/")
            .interceptors(FlipperInitializer.okHttpFlipperInterceptors)
            .build()
    }

    private val testListener = object : SmsInitializationListener {
        override fun onInitiated(
            data: SmsInitiationResponseData
        ) {
            logger.debug("Test app onInitiated $data")
        }

        override fun onInitializationFailed(t: Throwable) {
            logger.debug("Test app onInitializationFailed")
        }
    }

    private val flashCallTestListener = object : FlashCallInitializationListener {
        override fun onInitiated(
            data: FlashCallInitializationResponseData
        ) {
            logger.debug("Test app onInitiated $data")
        }

        override fun onInitializationFailed(t: Throwable) {
            logger.debug("Test app onInitializationFailed $t")
        }

    }

    private val testListenerVerification = object : VerificationListener {
        override fun onVerified() {
            logger.debug("Test app onVerified")
        }

        override fun onVerificationFailed(t: Throwable) {
            logger.debug("Test app onVerificationFailed $t")
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
        verification = SmsVerificationMethod.Builder.instance.config(
            SmsVerificationConfig.Builder.instance
                .globalConfig(globalConfig)
                .number(phoneNumber.text.toString())
                .honourEarlyReject(true)
                .custom("testCustom")
                .appHash("0wjBaTjBink")
                .maxTimeout(null)
                .build()
        )
            .initializationListener(testListener)
            .verificationListener(testListenerVerification)
            .build()
    }

}
