package com.sinch.verificationcore.verification.interceptor

import android.os.Handler

abstract class BasicCodeInterceptor(
    override val maxTimeout: Long?,
    override val interceptionListener: CodeInterceptionListener
) : CodeInterceptor {

    private val cancelHandler = Handler()

    final override fun start() {
        initializeCancelHandler()
        onInterceptorStarted()
    }

    final override fun stop() {
        cancelHandler.removeCallbacksAndMessages(null)
        onInterceptorStopped()
    }

    abstract fun onInterceptorStarted()
    abstract fun onInterceptorStopped()
    abstract fun onInterceptorTimedOut()

    private fun initializeCancelHandler() {
        maxTimeout?.let {
            cancelHandler.postDelayed({
                interceptionListener.onCodeInterceptionError(CodeInterceptionTimeoutException())
                onInterceptorTimedOut()
            }, it)
        }
    }
}