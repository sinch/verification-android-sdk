package com.sinch.verificationcore.internal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class VerificationStatus(val value: String) {
    @SerialName("SUCCESSFUL")
    SUCCESSFUL("SUCCESSFUL");

    companion object {
        fun forKey(value: String) = values().first { it.value == value }
    }
}