package com.sinch.sinchverification

import android.app.Application
import android.view.MenuItem
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

    fun onDevelopmentOptionSelected(item: MenuItem): Boolean {
        item.isChecked = true
        apiBaseURL = when (item.itemId) {
            R.id.productionApi -> BuildConfig.API_BASE_URL_PROD
            R.id.productionAPSE1 -> BuildConfig.API_BASE_URL_PROD_APSE1
            R.id.productionEUC1 -> BuildConfig.API_BASE_URL_PROD_EUC1
            R.id.ftest1Api -> BuildConfig.API_BASE_URL_FTEST1
            R.id.ftest2Api -> BuildConfig.API_BASE_URL_FTEST2
            R.id.customItemId -> ""
            else -> throw RuntimeException("Menu item with ${item.itemId} not handled")
        }
        appKey = when (item.itemId) {
            R.id.productionApi -> BuildConfig.APP_KEY_PROD
            R.id.productionAPSE1 -> BuildConfig.APP_KEY_PROD_APSE
            R.id.productionEUC1 -> BuildConfig.APP_KEY_PROD_EUC1
            R.id.ftest1Api -> BuildConfig.APP_KEY_FTEST1
            R.id.ftest2Api -> BuildConfig.APP_KEY_FTEST2
            R.id.customItemId -> ""
            else -> throw RuntimeException("Menu item with ${item.itemId} not handled")
        }
        childGlobalConfigPropertiesUpdateListener?.onAppKeyUpdated(appKey)
        childGlobalConfigPropertiesUpdateListener?.onBaseURLUpdated(
            baseURL = apiBaseURL,
            isCustom = (item.itemId == R.id.customItemId)
        )
        rebuildGlobalConfig()
        return true
    }

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