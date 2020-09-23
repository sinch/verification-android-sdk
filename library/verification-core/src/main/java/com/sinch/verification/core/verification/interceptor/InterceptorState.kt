package com.sinch.verification.core.verification.interceptor

/**
 * Enum defining states of [CodeInterceptor].
 */
enum class InterceptorState {
    /**
     * The Interceptor has not started yet.
     */
    IDLE,

    /**
     * The interceptor is during the process of intercepting codes.
     */
    STARTED,

    /**
     * The interceptor has finished not finish intercepting codes yet however it is past the interception timeout.
     */
    REPORTING,

    /**
     * The interceptor has stopped.
     */
    DONE;
}