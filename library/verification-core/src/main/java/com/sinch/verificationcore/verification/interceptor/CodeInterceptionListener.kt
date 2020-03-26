package com.sinch.verificationcore.verification.interceptor

interface CodeInterceptionListener {
    fun onCodeIntercepted(code: String)
    fun onCodeInterceptionError(e: Throwable)
}