package com.sinch.verification.core.verification.interceptor

import android.os.Handler
import com.sinch.logging.logger
import com.sinch.verification.core.internal.VerificationMethodType
import kotlin.properties.Delegates

/**
 * Common logic of each verification methods interceptors.
 */
abstract class BasicCodeInterceptor(
    timeoutInitial: Long,
    override val interceptionListener: CodeInterceptionListener,
    final override val method: VerificationMethodType
) : CodeInterceptor {

    protected val cancelHandler = Handler()
    protected val logger = logger()

    final override var state: InterceptorState = InterceptorState.IDLE
        private set

    /**
     * Flag indicating if interception timeout has passed.
     */
    val isPastInterceptionTimeout: Boolean get() = state == InterceptorState.REPORTING || state == InterceptorState.DONE

    /**
     * Flag indicating if the interceptor should automatically stop when [interceptionTimeout] has passed.
     */
    open val shouldInterceptorStopWhenTimedOut: Boolean = true

    private val delayedStopRunnable = Runnable {
        state = InterceptorState.REPORTING
        if (shouldInterceptorStopWhenTimedOut) {
            stop()
        }
        interceptionListener.onCodeInterceptionError(CodeInterceptionTimeoutException())
        onInterceptorTimedOut()
    }

    override var interceptionTimeout: Long by Delegates.observable(timeoutInitial) { _, _, _ ->
        if (state == InterceptorState.STARTED) {
            initializeCancelHandler()
        }
    }

    final override fun start() {
        if (state == InterceptorState.IDLE) {
            logger.debug("Code interceptor started")
            state = InterceptorState.STARTED
            initializeCancelHandler()
            onInterceptorStarted()
        } else {
            logger.debug("Interceptor already started")
        }
    }

    final override fun stop() {
        if (state != InterceptorState.DONE) {
            logger.debug("Code interceptor stopped")
            state = InterceptorState.DONE
            cancelHandler.removeCallbacks(delayedStopRunnable)
            onInterceptorStopped()
            interceptionListener.onCodeInterceptionStopped()
        } else {
            logger.debug("Interceptor already stopped")
        }
    }

    abstract fun onInterceptorStarted()
    abstract fun onInterceptorStopped()
    abstract fun onInterceptorTimedOut()

    private fun initializeCancelHandler() {
        if (state != InterceptorState.DONE) {
            logger.debug("Cancel handler initialized with timeout: $interceptionTimeout")
            cancelHandler.removeCallbacks(delayedStopRunnable)
            cancelHandler.postDelayed(delayedStopRunnable, interceptionTimeout)
        }
    }
}