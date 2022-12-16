package com.sinch.verification.core.auth

import android.util.Base64
import okhttp3.Request

/**
 * [AuthorizationMethod] that uses application key and application secret
 * to authorize API requests. To get the key and secret check your application page on
 * [Sinch Dashboard](https://portal.sinch.com/)
 * @param appKey Application key assigned to the app.
 * @param appSecret Application secret assigned to the app.
 *
 */
class BasicAuthorizationMethod(private val appKey: String, private val appSecret: String) :
    AuthorizationMethod {

    override fun onPrepareAuthorization() {
    }

    override fun onAuthorize(request: Request): Request {
        val data = Base64.encodeToString(("$appKey:$appSecret").encodeToByteArray(), Base64.NO_WRAP)
        return request.newBuilder().addHeader("Authorization", "Basic $data").build()
    }
}