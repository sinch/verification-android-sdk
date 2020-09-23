package com.sinch.verification.core.verification.response

/**
 * Convenient [VerificationListener] with empty method implementations.
 */
open class EmptyVerificationListener : VerificationListener {
    override fun onVerified() {}
    override fun onVerificationFailed(t: Throwable) {}
}