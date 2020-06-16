package com.sinch.verification.smssample

import android.app.Application
import com.sinch.verificationcore.auth.AppKeyAuthorizationMethod
import com.sinch.verificationcore.config.general.GlobalConfig
import com.sinch.verificationcore.config.general.SinchGlobalConfig


class VerificationSampleApp : Application() {

    val globalConfig: GlobalConfig by lazy {
        SinchGlobalConfig.Builder.instance.applicationContext(this)
            .authorizationMethod(AppKeyAuthorizationMethod(BuildConfig.APP_KEY))
            .build()
    }

}