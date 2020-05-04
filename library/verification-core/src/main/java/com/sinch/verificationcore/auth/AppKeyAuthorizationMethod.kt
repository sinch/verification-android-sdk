package com.sinch.verificationcore.auth

import okhttp3.Request

/**
 * [AuthorizationMethod] that uses application key to authorize API requests. To get the key check your application page on
 * [Sinch Dashboard](https://portal.sinch.com/)
 * @param appKey Application key assigned to the app.
 */
class AppKeyAuthorizationMethod(private val appKey: String) : AuthorizationMethod {

    override fun onPrepareAuthorization() {}

    override fun onAuthorize(request: Request): Request =
        request.newBuilder().addHeader("Authorization", "Application $appKey").build()

}