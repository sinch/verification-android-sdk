package com.sinch.sinchverification

import android.app.Application
import android.webkit.URLUtil
import com.sinch.logging.Log
import com.sinch.logging.LogcatAppender
import com.sinch.verification.core.auth.AppKeyAuthorizationMethod
import com.sinch.verification.core.config.general.GlobalConfig
import com.sinch.verification.core.config.general.SinchGlobalConfig
import okhttp3.logging.HttpLoggingInterceptor


class VerificationSampleApp : Application() {

    var apiBaseURL = BuildConfig.API_BASE_URL_PROD
        private set

    var appKey = BuildConfig.APP_KEY_PROD
        private set

    var globalConfig: GlobalConfig = buildGlobalConfig(
        apiHost = apiBaseURL,
        appKey = appKey
    )
        private set

    var childGlobalConfigPropertiesUpdateListener: GlobalConfigPropertiesUpdateListener? = null

    override fun onCreate() {
        super.onCreate()
        initFlipper()
        initLogger()
    }

    private fun initFlipper() {
        FlipperInitializer.initFlipperPlugins(this)
    }

    private fun initLogger() {
        Log.init(LogcatAppender(), EventBusAppender())
    }

    private fun buildGlobalConfig(apiHost: String, appKey: String): GlobalConfig =
        SinchGlobalConfig.Builder.instance.applicationContext(this)
            .authorizationMethod(AppKeyAuthorizationMethod(appKey))
            .apiHost(apiHost)
            .interceptors(FlipperInitializer.okHttpFlipperInterceptors + HttpLoggingInterceptor().apply {
                setLevel(
                    HttpLoggingInterceptor.Level.BODY
                )
            })
            .build()

    fun updateBaseUrlManually(newBaseURL: String) {
        this.apiBaseURL = newBaseURL
        rebuildGlobalConfig()
    }

    fun updateAppKeyManually(newAppKey: String) {
        this.appKey = newAppKey
        rebuildGlobalConfig()
    }

    private fun rebuildGlobalConfig() {
        if (!URLUtil.isValidUrl(apiBaseURL)) {
            return
        }
        globalConfig = buildGlobalConfig(
            apiHost = apiBaseURL,
            appKey = appKey
        )
    }

}