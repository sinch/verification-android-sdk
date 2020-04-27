package com.sinch.verificationcore.verification.interceptor

import android.os.Handler
import com.sinch.logging.logger
import kotlin.properties.Delegates

abstract class BasicCodeInterceptor(
    maxTimeoutInitial: Long,
    override val interceptionListener: CodeInterceptionListener
) : CodeInterceptor {

    protected val cancelHandler = Handler()
    protected val logger = logger()

    final override var state: InterceptorState = InterceptorState.IDLE
        private set
    val isPastInterceptionTimeout: Boolean get() = !cancelHandler.hasCallbacks(delayedStopRunnable)

    open val shouldInterceptorStopWhenTimedOut: Boolean = true

    private val delayedStopRunnable = Runnable {
        if (shouldInterceptorStopWhenTimedOut) {
            stop()
        }
        interceptionListener.onCodeInterceptionError(CodeInterceptionTimeoutException())
        onInterceptorTimedOut()
    }


    override var maxTimeout: Long by Delegates.observable(maxTimeoutInitial) { _, _, _ ->
        if (state == InterceptorState.STARTED) {
            initializeCancelHandler()
        }
    }

    final override fun start() {
        state = InterceptorState.STARTED
        initializeCancelHandler()
        onInterceptorStarted()
    }

    final override fun stop() {
        state = InterceptorState.DONE
        cancelHandler.removeCallbacks(delayedStopRunnable)
        onInterceptorStopped()
        interceptionListener.onCodeInterceptionStopped()
    }

    abstract fun onInterceptorStarted()
    abstract fun onInterceptorStopped()
    abstract fun onInterceptorTimedOut()

    private fun initializeCancelHandler() {
        if (state != InterceptorState.DONE) {
            cancelHandler.removeCallbacks(delayedStopRunnable)
            cancelHandler.postDelayed(delayedStopRunnable, maxTimeout)
        }
    }
}