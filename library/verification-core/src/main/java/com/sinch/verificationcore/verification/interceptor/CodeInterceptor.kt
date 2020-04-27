package com.sinch.verificationcore.verification.interceptor

internal interface CodeInterceptor {
    var maxTimeout: Long
    val interceptionListener: CodeInterceptionListener
    val state: InterceptorState
    fun start()
    fun stop()
}