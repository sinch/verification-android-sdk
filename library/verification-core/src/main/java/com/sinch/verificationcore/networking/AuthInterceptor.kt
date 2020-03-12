package com.sinch.verificationcore.networking

import com.sinch.verificationcore.auth.AuthMethod
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val authMethod: AuthMethod) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response =
        chain.proceed(authMethod.onAuthorize(chain.request()))

}