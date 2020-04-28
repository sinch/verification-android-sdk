@file:Suppress("unused")

package com.sinch.verificationcore.verification

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class VerificationSourceType(val value: String) {
    @SerialName("intercept")
    INTERCEPTION("intercept"),

    @SerialName("manual")
    MANUAL("manual"),

    @SerialName ("log")
    LOG("log");

    companion object {
        fun forKey(value: String) = values().first { it.value == value }
    }
}