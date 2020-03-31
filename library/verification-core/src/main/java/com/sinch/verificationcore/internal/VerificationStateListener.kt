package com.sinch.verificationcore.internal

import com.sinch.verificationcore.internal.error.VerificationState

interface VerificationStateListener {
    fun update(newState: VerificationState)
}