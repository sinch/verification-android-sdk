package com.sinch.verification.callout.verification.interceptor

import com.sinch.verification.callout.CalloutVerificationMethod
import com.sinch.verificationcore.verification.interceptor.BasicCodeInterceptor
import com.sinch.verificationcore.verification.interceptor.CodeInterceptionListener
import com.sinch.verificationcore.verification.interceptor.CodeInterceptionTimeoutException
import com.sinch.verificationcore.verification.response.VerificationListener

/**
 * Code interceptor for [CalloutVerificationMethod]. This interceptor does not intercept any calls automatically.
 * It's job is only to notify [VerificationListener] about [CodeInterceptionTimeoutException] if [maxTimeout] ms have passed.
 * @param maxTimeout Maximum timeout in milliseconds after which [CodeInterceptionTimeoutException] is passed to the [VerificationListener]
 * @param interceptionListener Listener to be notified about the interception process results.
 */
class CalloutInterceptor(
    maxTimeout: Long,
    interceptionListener: CodeInterceptionListener
) :
    BasicCodeInterceptor(maxTimeout, interceptionListener) {

    override fun onInterceptorStarted() {}

    override fun onInterceptorStopped() {}

    override fun onInterceptorTimedOut() {}
}