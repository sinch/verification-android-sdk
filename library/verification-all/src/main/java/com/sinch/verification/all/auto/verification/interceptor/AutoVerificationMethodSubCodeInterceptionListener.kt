package com.sinch.verification.all.auto.verification.interceptor

import com.sinch.logging.logger
import com.sinch.verification.core.internal.VerificationMethodType
import com.sinch.verification.core.verification.VerificationSourceType
import com.sinch.verification.core.verification.interceptor.CodeInterceptionListener

/**
 * [CodeInterceptionListener] responsible for passing code interception results from specific
 * verification methods (sms, flashcall etc.) to parent interceptor.
 */
internal class AutoVerificationMethodSubCodeInterceptionListener(
    private val method: VerificationMethodType,
    private val subCodeInterceptionListener: SubCodeInterceptionListener
) :
    CodeInterceptionListener {

    private val logger = logger()

    override fun onCodeIntercepted(code: String, source: VerificationSourceType) {
        logger.debug(
            "Intercepted verification results from method: $method, " +
                    "verification code is $code"
        )
        subCodeInterceptionListener.onSubCodeIntercepted(
            code,
            method = method,
            source = VerificationSourceType.INTERCEPTION
        )
    }

    override fun onCodeInterceptionError(e: Throwable) {
        subCodeInterceptionListener.onSubCodeInterceptionError(e, method)
    }

    override fun onCodeInterceptionStopped() {
        subCodeInterceptionListener.onSubCodeInterceptionStopped(method)
    }

}