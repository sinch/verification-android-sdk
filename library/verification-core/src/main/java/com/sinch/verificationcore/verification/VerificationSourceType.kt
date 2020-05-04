@file:Suppress("unused")

package com.sinch.verificationcore.verification

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Enum representing different possible ways the verification code might be collected.
 */
@Serializable
enum class VerificationSourceType(val value: String) {

    /**
     * Code was automatically intercepted by the interceptor.
     */
    @SerialName("intercept")
    INTERCEPTION("intercept"),

    /**
     * Code was manually typed by the user.
     */
    @SerialName("manual")
    MANUAL("manual"),

    /**
     * Code was grabbed from the log. For example from the call history for flashcall verification method.
     */
    @SerialName("log")
    LOG("log");

    companion object {
        fun forKey(value: String) = values().first { it.value == value }
    }
}