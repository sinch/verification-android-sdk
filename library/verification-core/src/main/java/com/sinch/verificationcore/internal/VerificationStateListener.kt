package com.sinch.verificationcore.internal

interface VerificationStateListener {
    fun update(newState: VerificationState)
    val verificationState: VerificationState
}