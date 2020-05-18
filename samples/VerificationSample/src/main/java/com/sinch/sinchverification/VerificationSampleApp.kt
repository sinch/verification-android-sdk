package com.sinch.sinchverification

import android.app.Application
import com.sinch.logging.Log
import com.sinch.logging.LogcatAppender
import com.sinch.verificationcore.auth.AppKeyAuthorizationMethod
import com.sinch.verificationcore.config.general.GlobalConfig
import com.sinch.verificationcore.config.general.SinchGlobalConfig
import okhttp3.logging.HttpLoggingInterceptor


class VerificationSampleApp : Application() {

    val globalConfig: GlobalConfig by lazy {
        SinchGlobalConfig.Builder.instance.applicationContext(this)
            //.authorizationMethod(AppKeyAuthorizationMethod("de23e021-db44-4004-902c-5a7fc18e35e2"))
            .authorizationMethod(AppKeyAuthorizationMethod("9e556452-e462-4006-aab0-8165ca04de66"))
            .interceptors(FlipperInitializer.okHttpFlipperInterceptors + HttpLoggingInterceptor().apply {
                setLevel(
                    HttpLoggingInterceptor.Level.BODY
                )
            })
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        initFlipper()
        initLogger()
    }

    private fun initFlipper() {
        FlipperInitializer.initFlipperPlugins(this)
    }

    private fun initLogger() {
        if (BuildConfig.DEBUG) {
            Log.init(LogcatAppender())
        }
    }

}