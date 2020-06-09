package com.sinch.sinchverification

import android.app.Application
import android.view.MenuItem
import com.sinch.logging.Log
import com.sinch.logging.LogcatAppender
import com.sinch.verificationcore.auth.AppKeyAuthorizationMethod
import com.sinch.verificationcore.config.general.GlobalConfig
import com.sinch.verificationcore.config.general.SinchGlobalConfig
import okhttp3.logging.HttpLoggingInterceptor


class VerificationSampleApp : Application() {

    val globalConfig: GlobalConfig by lazy {
        SinchGlobalConfig.Builder.instance.applicationContext(this)
            .authorizationMethod(AppKeyAuthorizationMethod(BuildConfig.APP_KEY))
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

    fun onDevelopmentOptionSelected(item: MenuItem): Boolean {
        //Nothing here - this callback is used on internal builds for testing purposes
        return false
    }

}