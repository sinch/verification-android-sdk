package com.sinch.verification.smssample

import android.app.Application
import com.sinch.verification.core.auth.AppKeyAuthorizationMethod
import com.sinch.verification.core.config.general.GlobalConfig
import com.sinch.verification.core.config.general.SinchGlobalConfig


class VerificationSampleApp : Application() {

    val globalConfig: GlobalConfig by lazy {
        SinchGlobalConfig.Builder.instance.applicationContext(this)
            .authorizationMethod(AppKeyAuthorizationMethod(BuildConfig.APP_KEY))
            .build()
    }

}