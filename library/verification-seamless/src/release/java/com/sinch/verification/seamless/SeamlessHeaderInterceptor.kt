package com.sinch.verification.seamless

import okhttp3.Interceptor
import okhttp3.Response

/**
 * No-op interceptor for release builds.
 */
class SeamlessHeaderInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(chain.request())
    }
}
