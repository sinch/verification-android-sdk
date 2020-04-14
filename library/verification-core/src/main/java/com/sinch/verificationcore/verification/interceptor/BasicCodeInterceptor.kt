package com.sinch.verificationcore.verification.interceptor

import android.os.Handler
import com.sinch.logging.logger

abstract class BasicCodeInterceptor(
    override val maxTimeout: Long?,
    override val interceptionListener: CodeInterceptionListener
) : CodeInterceptor {

    protected val logger = logger()
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
                stop()
                interceptionListener.onCodeInterceptionError(CodeInterceptionTimeoutException())
                onInterceptorTimedOut()
            }, it)
        }
    }
}