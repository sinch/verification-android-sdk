package com.sinch.verification.core.internal

import com.sinch.verification.core.initiation.response.InitiationResponseData

/**
 * Current state of the verification process.
 */
sealed class VerificationState {

    /**
     * Verification instance has been constructed but the process has not started yet.
     */
    object IDLE : VerificationState()

    /**
     * Verification instance has been constructed but it was manually stopped.
     */
    object ManuallyStopped : VerificationState()

    /**
     * The process of initialization has started.
     * @property status Current status of initialization.
     */
    data class Initialization(val status: VerificationStateStatus, val initiationResponseData: InitiationResponseData?) : VerificationState()

    /**
     * The process of verification has started. This state is set once the [Verification] verify method is called.
     * @property status Current status of the verification.
     */
    data class Verification(val status: VerificationStateStatus) : VerificationState()

    /**
     * Flag indicating if the verification process has finished successfully.
     */
    val isVerified: Boolean get() = this == Verification(VerificationStateStatus.SUCCESS)

    /**
     * Flag indicating if the the verification process can be initiated.
     */
    val canInitiate: Boolean
        get() = when (this) {
            IDLE -> true
            is Initialization -> this.status.isFinished
            else -> false
        }

    /**
     * Flag indicating if new verification code can be verified.
     */
    val canVerify: Boolean
        get() = when (this) {
            is Verification -> this.status != VerificationStateStatus.SUCCESS
            else -> true
        }

    /**
     * Flag indicating if the verification process has been completed (either with error or success).
     */
    val isVerificationProcessFinished: Boolean
        get() = when (this) {
            is Verification -> this.status.isFinished
            is Initialization -> this.status == VerificationStateStatus.ERROR
            ManuallyStopped -> true
            IDLE -> false
        }
}