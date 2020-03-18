package com.sinch.verificationcore.auth

import okhttp3.Request

interface AuthorizationMethod {
    fun onPrepareAuthorization()
    fun onAuthorize(request: Request): Request
}