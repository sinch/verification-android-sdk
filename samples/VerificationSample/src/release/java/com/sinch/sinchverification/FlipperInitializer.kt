package com.sinch.sinchverification

import android.content.Context
import okhttp3.Interceptor

object FlipperInitializer {

    val okHttpFlipperInterceptors by lazy {
        emptyList<Interceptor>()
    }

    fun initFlipperPlugins(@Suppress("UNUSED_PARAMETER") context: Context) {}

}