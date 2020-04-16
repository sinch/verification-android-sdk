package com.sinch.sinchverification

import android.content.Context
import com.facebook.flipper.android.AndroidFlipperClient
import com.facebook.flipper.android.utils.FlipperUtils
import com.facebook.flipper.plugins.inspector.DescriptorMapping
import com.facebook.flipper.plugins.inspector.InspectorFlipperPlugin
import com.facebook.flipper.plugins.network.FlipperOkhttpInterceptor
import com.facebook.flipper.plugins.network.NetworkFlipperPlugin
import com.facebook.soloader.SoLoader
import okhttp3.Interceptor

object FlipperInitializer {

    private val networkPlugin = NetworkFlipperPlugin()

    val okHttpFlipperInterceptors by lazy {
        listOf<Interceptor>(FlipperOkhttpInterceptor(networkPlugin))
    }

    fun initFlipperPlugins(context: Context) {
        SoLoader.init(context, false)
        if (FlipperUtils.shouldEnableFlipper(context)) {
            AndroidFlipperClient.getInstance(context).apply {
                addPlugin(
                    InspectorFlipperPlugin(
                        context,
                        DescriptorMapping.withDefaults()
                    )
                )
                addPlugin(networkPlugin)
            }.start()
        }
    }

}