package com.sinch.verificationcore.auth

import okhttp3.Request

class AppKeyAuthMethod(private val appKey: String) : AuthMethod {

    override fun onPrepareAuth() {}

    override fun onAuthorize(request: Request): Request =
        request.newBuilder().addHeader("Authorization", "Application $appKey").build()

}