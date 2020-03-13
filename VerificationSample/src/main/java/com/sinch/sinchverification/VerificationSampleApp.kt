package com.sinch.sinchverification

import android.app.Application
import com.facebook.flipper.android.AndroidFlipperClient
import com.facebook.flipper.android.utils.FlipperUtils
import com.facebook.flipper.plugins.inspector.DescriptorMapping
import com.facebook.flipper.plugins.inspector.InspectorFlipperPlugin
import com.facebook.flipper.plugins.network.NetworkFlipperPlugin
import com.facebook.soloader.SoLoader


class VerificationSampleApp : Application() {

    val networkPlugin = NetworkFlipperPlugin()

    override fun onCreate() {
        super.onCreate()
        initFlipper()
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

}