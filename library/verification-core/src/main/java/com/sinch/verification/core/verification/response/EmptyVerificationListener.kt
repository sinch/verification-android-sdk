package com.sinch.verification.core.verification.response

import com.sinch.verification.core.verification.VerificationEvent

/**
 * Convenient [VerificationListener] with empty method implementations.
 */
open class EmptyVerificationListener : VerificationListener {
    override fun onVerified() {}
    override fun onVerificationFailed(t: Throwable) {}
    override fun onVerificationEvent(event: VerificationEvent) {}
}