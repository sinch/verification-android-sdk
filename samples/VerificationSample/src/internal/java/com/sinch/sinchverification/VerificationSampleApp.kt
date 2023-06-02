package com.sinch.sinchverification

import android.app.Application
import android.webkit.URLUtil
import com.sinch.logging.Log
import com.sinch.logging.appenders.FileAppender
import com.sinch.logging.appenders.LogcatAppender
import com.sinch.sinchverification.utils.appenders.EventBusAppender
import com.sinch.sinchverification.utils.HttpFileLogger
import com.sinch.sinchverification.utils.SharedPrefsManager
import com.sinch.sinchverification.utils.appenders.LogOverlayAppender
import com.sinch.sinchverification.utils.logoverlay.LogOverlay
import com.sinch.verification.core.auth.AppKeyAuthorizationMethod
import com.sinch.verification.core.config.general.GlobalConfig
import com.sinch.verification.core.config.general.SinchGlobalConfig
import okhttp3.logging.HttpLoggingInterceptor


class VerificationSampleApp : Application() {

    lateinit var globalConfig: GlobalConfig
        private set

    private val sharedPrefsManager: SharedPrefsManager by lazy {
        SharedPrefsManager(this)
    }

    val usedConfig get() = sharedPrefsManager.usedConfig

    override fun onCreate() {
        super.onCreate()
        initFlipper()
        initLogger()
        rebuildGlobalConfig()
    }

    private fun initFlipper() {
        FlipperInitializer.initFlipperPlugins(this)
    }

    private fun initLogger() {
        LogOverlay.init(this)
        Log.init(LogcatAppender(), EventBusAppender(), FileAppender(this), LogOverlayAppender())
    }

    private fun buildGlobalConfig(apiHost: String, appKey: String): GlobalConfig =
        SinchGlobalConfig.Builder.instance.applicationContext(this)
            .authorizationMethod(AppKeyAuthorizationMethod(appKey))
            .apiHost(apiHost)
            .interceptors(FlipperInitializer.okHttpFlipperInterceptors +
                HttpLoggingInterceptor().apply {
                    setLevel(
                        HttpLoggingInterceptor.Level.BODY
                    )
                } +
                HttpLoggingInterceptor(HttpFileLogger()).apply {
                    setLevel(
                        HttpLoggingInterceptor.Level.BODY
                    )
                })
            .build()

    fun updateCurrentConfigKey(newAppKey: String) {
        sharedPrefsManager.usedConfig = sharedPrefsManager.usedConfig.copy(appKey = newAppKey)
        rebuildGlobalConfig()
    }

    fun updateCurrentConfigBaseURL(newEnv: String) {
        sharedPrefsManager.usedConfig = sharedPrefsManager.usedConfig.copy(environment = newEnv)
        rebuildGlobalConfig()
    }

    fun updateAppConfig(newConfigName: String) {
        sharedPrefsManager.usedConfigName = newConfigName
        rebuildGlobalConfig()
    }

    private fun rebuildGlobalConfig() {
        val usedConfig = sharedPrefsManager.usedConfig
        if (!URLUtil.isValidUrl(usedConfig.environment)) {
            return
        }
        globalConfig = buildGlobalConfig(
            apiHost = usedConfig.environment,
            appKey = usedConfig.appKey
        )
    }

}