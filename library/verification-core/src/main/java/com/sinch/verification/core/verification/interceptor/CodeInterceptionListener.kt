package com.sinch.verification.core.verification.interceptor

import com.sinch.verification.core.verification.VerificationSourceType

/**
 * Listener defining callbacks invoked by [CodeInterceptor] informing about the code interception process.
 */
interface CodeInterceptionListener {

    /**
     * Called when the verification code has been successfully intercepted.
     * @param code Intercepted verification code.
     * @param source Source of the code.
     */
    fun onCodeIntercepted(
        code: String,
        source: VerificationSourceType = VerificationSourceType.INTERCEPTION
    )

    /**
     * Called when [CodeInterceptor] reported an error.
     * @param e Error describing what went wrong.
     */
    fun onCodeInterceptionError(e: Throwable)

    /**
     * Called after the interceptor has stopped. This callback might be used to report the interception process result.
     */
    fun onCodeInterceptionStopped()
}