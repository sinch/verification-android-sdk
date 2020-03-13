package com.sinch.verificationcore.auth

import okhttp3.Interceptor
import okhttp3.Response

class AuthorizationInterceptor(private val authorizationMethod: AuthorizationMethod) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response =
        chain.proceed(authorizationMethod.onAuthorize(chain.request()))

}