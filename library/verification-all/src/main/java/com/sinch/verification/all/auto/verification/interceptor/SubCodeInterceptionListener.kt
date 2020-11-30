package com.sinch.verification.all.auto.verification.interceptor

import com.sinch.verification.core.internal.VerificationMethodType
import com.sinch.verification.core.verification.VerificationSourceType
import com.sinch.verification.core.verification.interceptor.CodeInterceptor

/**
 * Listener defining callbacks invoked by [CodeInterceptor]s that are used by[AutoVerificationInterceptor].
 */
interface SubCodeInterceptionListener {

    /**
     * Called when the verification code has been successfully intercepted by subinterceptor.
     * @param code Intercepted verification code.
     * @param method Verification method used to intercept the code.
     * @param source Source of the code.
     */
    fun onSubCodeIntercepted(
        code: String,
        method: VerificationMethodType,
        source: VerificationSourceType
    )

    /**
     * Called when sub[CodeInterceptor] reported an error.
     * @param e Error describing what went wrong.
     * @param method Verification method that was used by the interceptor.
     */
    fun onSubCodeInterceptionError(e: Throwable, method: VerificationMethodType)

    /**
     * Called after the subinterceptor has stopped.
     */
    fun onSubCodeInterceptionStopped(method: VerificationMethodType)

}