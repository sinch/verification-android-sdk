@file:Suppress("unused")

package com.sinch.verification.core.internal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Enum describing API status of the verification.
 */
@Serializable
enum class VerificationStatus(val value: String) {

    /**
     * Verification is ongoing.
     */
    @SerialName("PENDING")
    PENDING("PENDING"),

    /**
     * Verification has been successfully processed.
     */
    @SerialName("SUCCESSFUL")
    SUCCESSFUL("SUCCESSFUL"),

    /**
     * Verification attempt was made, but the number was not verified.
     */
    @SerialName("FAIL")
    FAILED("FAIL"),

    /**
     * Verification attempt was denied by Sinch or your backend.
     */
    @SerialName("DENIED")
    DENIED("DENIED"),

    /**
     * Verification attempt was aborted.
     */
    @SerialName("DENIED")
    ABORTED("DENIED"),

    /**
     * Verification attempt could not be completed due to a network error or the number being unreachable.
     */
    @SerialName("ERROR")
    ERROR("ERROR");

    companion object {
        fun forKey(value: String) = values().first { it.value == value }
    }
}