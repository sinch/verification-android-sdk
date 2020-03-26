package com.sinch.sinchverification

import android.app.Application
import com.facebook.flipper.android.AndroidFlipperClient
import com.facebook.flipper.android.utils.FlipperUtils
import com.facebook.flipper.plugins.inspector.DescriptorMapping
import com.facebook.flipper.plugins.inspector.InspectorFlipperPlugin
import com.facebook.flipper.plugins.network.NetworkFlipperPlugin
import com.facebook.soloader.SoLoader
import com.sinch.logging.Log
import com.sinch.logging.LogcatAppender


class VerificationSampleApp : Application() {

    val networkPlugin = NetworkFlipperPlugin()

    override fun onCreate() {
        super.onCreate()
        initFlipper()
        initLogger()
    }

    private fun initFlipper() {
        SoLoader.init(this, false)
        if (BuildConfig.DEBUG && FlipperUtils.shouldEnableFlipper(this)) {
            AndroidFlipperClient.getInstance(this).apply {
                addPlugin(
                    InspectorFlipperPlugin(
                        this@VerificationSampleApp,
                        DescriptorMapping.withDefaults()
                    )
                )
                addPlugin(networkPlugin)
            }.start()
        }
    }

    private fun initLogger() {
        if (BuildConfig.DEBUG) {
            Log.init(LogcatAppender())
        }
    }

}