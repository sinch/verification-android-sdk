package com.sinch.verificationcore.auth

import okhttp3.Request

class AppKeyAuthorizationMethod(private val appKey: String) : AuthorizationMethod {

    override fun onPrepareAuthorization() {}

    override fun onAuthorize(request: Request): Request =
        request.newBuilder().addHeader("Authorization", "Application $appKey").build()

}