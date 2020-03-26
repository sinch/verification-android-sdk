package com.sinch.verificationcore.verification.response

interface VerificationListener {
    fun onVerified()
    fun onVerificationFailed(t: Throwable)
}