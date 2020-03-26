package com.sinch.verificationcore.verification.response

open class EmptyVerificationListener : VerificationListener {
    override fun onVerified() {}
    override fun onVerificationFailed(t: Throwable) {}
}