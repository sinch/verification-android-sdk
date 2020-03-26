package com.sinch.verificationcore.verification.interceptor

internal interface CodeInterceptor {
    val maxTimeout: Long?
    val interceptionListener: CodeInterceptionListener
    fun start()
    fun stop()
}