package com.sinch.sinchverification

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.facebook.flipper.plugins.network.FlipperOkhttpInterceptor
import com.google.android.material.snackbar.Snackbar
import com.sinch.smsverification.SmsVerificationMethod
import com.sinch.smsverification.config.SmsVerificationConfig
import com.sinch.smsverification.initialization.SmsInitializationListener
import com.sinch.smsverification.initialization.SmsInitiationResponseData
import com.sinch.verificationcore.auth.AppKeyAuthorizationMethod
import com.sinch.verificationcore.config.general.SinchGeneralConfig
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.Interceptor
import timber.log.Timber


class MainActivity : AppCompatActivity() {

    val app: VerificationSampleApp get() = application as VerificationSampleApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        val globalConfig = SinchGeneralConfig.Builder()
            .context(applicationContext)
            .apiHost("https://verificationapi-v1.sinch.com/")
            .interceptors(listOf<Interceptor>(FlipperOkhttpInterceptor(app.networkPlugin)))
            .authMethod(AppKeyAuthorizationMethod("9e556452-e462-4006-aab0-8165ca04de66")).build()

        val testListener = object : SmsInitializationListener {
            override fun onInitiated(data: SmsInitiationResponseData) {
                Timber.d("Test app onInitiated")
            }

            override fun onInitializationFailed(t: Throwable) {
                Timber.d("Test app onInitializationFailed")
            }
        }

        SmsVerificationMethod(
            SmsVerificationConfig(
                config = globalConfig,
                number = "+48509873255",
                custom = "testCustom"
            ),
            initializationListener = testListener
        ).initiate()
    }

}
