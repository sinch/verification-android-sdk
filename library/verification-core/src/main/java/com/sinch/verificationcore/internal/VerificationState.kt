package com.sinch.verificationcore.internal

sealed class VerificationState {
    object IDLE : VerificationState()
    data class Initialization(val status: VerificationStateStatus) : VerificationState()
    data class Verification(val status: VerificationStateStatus) : VerificationState()

    val isVerified: Boolean get() = this == Verification(VerificationStateStatus.SUCCESS)

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