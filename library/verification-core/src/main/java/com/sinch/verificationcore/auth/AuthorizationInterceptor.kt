package com.sinch.verificationcore.auth

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Okhttp interceptor that uses specified [AuthorizationMethod] to authorize API requests.
 * @param authorizationMethod Method used to authorize the call.
 */
class AuthorizationInterceptor(private val authorizationMethod: AuthorizationMethod) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response =
        chain.proceed(authorizationMethod.onAuthorize(chain.request()))

}