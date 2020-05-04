package com.sinch.verificationcore.verification.interceptor

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
     * The interceptor has stopped.
     */
    DONE;
}