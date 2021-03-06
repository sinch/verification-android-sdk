package com.sinch.verification.callout.verification.interceptor

import com.sinch.verification.callout.CalloutVerificationMethod
import com.sinch.verification.core.internal.VerificationMethodType
import com.sinch.verification.core.verification.interceptor.BasicCodeInterceptor
import com.sinch.verification.core.verification.interceptor.CodeInterceptionListener
import com.sinch.verification.core.verification.interceptor.CodeInterceptionTimeoutException
import com.sinch.verification.core.verification.response.VerificationListener

/**
 * Code interceptor for [CalloutVerificationMethod]. This interceptor does not intercept any calls automatically.
 * It's job is only to notify [VerificationListener] about [CodeInterceptionTimeoutException] if [interceptionTimeout] ms have passed.
 * @param interceptionTimeout Maximum timeout in milliseconds after which [CodeInterceptionTimeoutException] is passed to the [VerificationListener]
 * @param interceptionListener Listener to be notified about the interception process results.
 */
class CalloutInterceptor(
    interceptionTimeout: Long,
    interceptionListener: CodeInterceptionListener
) :
    BasicCodeInterceptor(
        interceptionTimeout,
        interceptionListener,
        VerificationMethodType.CALLOUT
    ) {

    override fun onInterceptorStarted() {}

    override fun onInterceptorStopped() {}

    override fun onInterceptorTimedOut() {}
}