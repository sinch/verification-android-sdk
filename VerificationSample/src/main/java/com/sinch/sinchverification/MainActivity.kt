package com.sinch.sinchverification

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.facebook.flipper.plugins.network.FlipperOkhttpInterceptor
import com.google.android.material.snackbar.Snackbar
import com.sinch.smsverification.SmsVerificationConfig
import com.sinch.smsverification.SmsVerificationMethod
import com.sinch.verificationcore.auth.AppKeyAuthorizationMethod
import com.sinch.verificationcore.config.general.SinchGeneralConfig
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.Interceptor


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
            .apiHost("https://verificationapi-v1-01.sinchlab.com/verification/v1/")
            .interceptors(listOf<Interceptor>(FlipperOkhttpInterceptor(app.networkPlugin)))
            .authMethod(AppKeyAuthorizationMethod("testKey")).build()

        SmsVerificationMethod(SmsVerificationConfig(globalConfig, "", "")).initiate()
    }

}
