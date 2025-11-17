package com.sinch.verification.seamless

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Debug-only interceptor for adding debug headers to targetUri requests.
 * Include this only in debug builds.
 */
class SeamlessHeaderInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
        
        // Only add headers if values are non-empty
        if (BuildConfig.DEBUG_MSISDN.isNotEmpty()) {
            requestBuilder.addHeader("x-msisdn", BuildConfig.DEBUG_MSISDN)
        }
        if (BuildConfig.DEBUG_IMSI.isNotEmpty()) {
            requestBuilder.addHeader("x-imsi", BuildConfig.DEBUG_IMSI)
        }
        
        return chain.proceed(requestBuilder.build())
    }
}
