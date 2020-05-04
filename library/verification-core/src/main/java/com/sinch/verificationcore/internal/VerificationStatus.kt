@file:Suppress("unused")

package com.sinch.verificationcore.internal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Enum describing API status of the verification.
 */
@Serializable
enum class VerificationStatus(val value: String) {

    /**
     * Verification has been successfully processed.
     */
    @SerialName("SUCCESSFUL")
    SUCCESSFUL("SUCCESSFUL"),

    /**
     * Verification has failed.
     */
    @SerialName("FAIL")
    FAILED("FAIL");

    companion object {
        fun forKey(value: String) = values().first { it.value == value }
    }
}