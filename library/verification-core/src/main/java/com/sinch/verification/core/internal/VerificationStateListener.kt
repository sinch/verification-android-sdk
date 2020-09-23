package com.sinch.verification.core.internal

interface VerificationStateListener {
    fun update(newState: VerificationState)
    val verificationState: VerificationState
}