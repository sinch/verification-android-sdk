package com.sinch.sinchverification

import android.app.Application
import android.view.MenuItem
import com.sinch.logging.Log
import com.sinch.logging.LogcatAppender
import com.sinch.verification.core.auth.AppKeyAuthorizationMethod
import com.sinch.verification.core.config.general.GlobalConfig
import com.sinch.verification.core.config.general.SinchGlobalConfig
import okhttp3.logging.HttpLoggingInterceptor


class VerificationSampleApp : Application() {

    var globalConfig: GlobalConfig = buildGlobalConfig(
        apiHost = BuildConfig.API_BASE_URL_PROD,
        appKey = BuildConfig.APP_KEY
    )
        private set

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
        globalConfig = when (item.itemId) {
            R.id.productionApi -> buildGlobalConfig(
                BuildConfig.API_BASE_URL_PROD,
                BuildConfig.APP_KEY_PROD
            )
            R.id.productionBrokApi -> buildGlobalConfig(
                BuildConfig.API_BASE_URL_PROD_BROK,
                BuildConfig.APP_KEY_PROD_BROK
            )
            R.id.ftest1Api -> buildGlobalConfig(
                BuildConfig.API_BASE_URL_FTEST1,
                BuildConfig.APP_KEY_FTEST1
            )
            R.id.ftest1BrokApi -> buildGlobalConfig(
                BuildConfig.API_BASE_URL_FTEST1_BROK,
                BuildConfig.APP_KEY_FTEST1_BROK
            )
            R.id.ftest2Api -> buildGlobalConfig(
                BuildConfig.API_BASE_URL_FTEST2,
                BuildConfig.APP_KEY_FTEST2
            )
            R.id.ftest2BrokApi -> buildGlobalConfig(
                BuildConfig.API_BASE_URL_FTEST2_BROK,
                BuildConfig.APP_KEY_FTEST2_BROK
            )
            else -> throw RuntimeException("Menu item with ${item.itemId} not handled")
        }
        return true
    }

}