package com.sinch.verificationcore.internal.error

import com.sinch.verificationcore.internal.VerificationStateStatus

sealed class VerificationState {
    object IDLE : VerificationState()
    data class Initialization(val status: VerificationStateStatus) : VerificationState()
    data class Verification(val status: VerificationStateStatus) : VerificationState()

    val canInitiate: Boolean
        get() = when (this) {
            IDLE -> true
            is Initialization -> this.status.isFinished
            else -> false
        }

    val canVerify: Boolean
        get() = when (this) {
            is Verification -> this.status != VerificationStateStatus.SUCCESS
            else -> true
        }
}