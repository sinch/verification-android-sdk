package com.sinch.verificationcore.internal

/**
 * Enum defining different statuses of [VerificationState]
 */
enum class VerificationStateStatus {

    /**
     * State is "active" meaning that probably an API call is being made and the result is not known yet.
     */
    ONGOING,

    /**
     * The step has been successfully processed.
     */
    SUCCESS,

    /**
     * The step has not been successfully processed.
     */
    ERROR;

    /**
     * Flag indicating if state result has been resolved.
     */
    val isFinished: Boolean get() = this != ONGOING
}