package com.sinch.sinchverification

import android.app.Application
import android.webkit.URLUtil
import com.sinch.logging.Log
import com.sinch.logging.appenders.FileAppender
import com.sinch.logging.appenders.LogcatAppender
import com.sinch.sinchverification.utils.HttpFileLogger
import com.sinch.sinchverification.utils.SharedPrefsManager
import com.sinch.verification.core.auth.AppKeyAuthorizationMethod
import com.sinch.verification.core.config.general.GlobalConfig
import com.sinch.verification.core.config.general.SinchGlobalConfig
import okhttp3.logging.HttpLoggingInterceptor


class VerificationSampleApp : Application() {

    lateinit var globalConfig: GlobalConfig
        private set

    var selectedEnvironment = Environment.PRODUCTION
        private set

    val usedApplicationKey: String
        get() = sharedPrefsManager.appKey(selectedEnvironment)
            .ifEmpty { selectedEnvironment.predefinedAppKey }

    val usedBaseUrl: String
        get() {
            return when (selectedEnvironment) {
                Environment.CUSTOM -> sharedPrefsManager.customURL
                else -> selectedEnvironment.predefinedURL
            }
        }

    var childGlobalConfigPropertiesUpdateListener: GlobalConfigPropertiesUpdateListener? = null

    private val sharedPrefsManager: SharedPrefsManager by lazy {
        SharedPrefsManager(this)
    }

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
        Log.init(LogcatAppender(), EventBusAppender(), FileAppender(this))
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

    fun updateSelectedEnvironment(newEnvironment: Environment) {
        this.selectedEnvironment = newEnvironment
        rebuildGlobalConfig()
    }

    fun updateBaseUrlManually(newBaseURL: String) {
        sharedPrefsManager.customURL = newBaseURL
        rebuildGlobalConfig()
    }

    fun updateAppKeyManually(newAppKey: String) {
        sharedPrefsManager.setAppKey(selectedEnvironment, newAppKey)
        rebuildGlobalConfig()
    }

    private fun rebuildGlobalConfig() {
        if (!URLUtil.isValidUrl(usedBaseUrl)) {
            return
        }
        globalConfig = buildGlobalConfig(
            apiHost = usedBaseUrl,
            appKey = usedApplicationKey
        )
    }

}