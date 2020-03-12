package com.sinch.verificationcore.auth

import okhttp3.Request

interface AuthMethod {
    fun onPrepareAuth()
    fun onAuthorize(request: Request): Request
}