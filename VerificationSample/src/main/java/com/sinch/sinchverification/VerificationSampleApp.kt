package com.sinch.sinchverification

import android.app.Application
import com.sinch.logging.Log
import com.sinch.logging.LogcatAppender


class VerificationSampleApp : Application() {

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