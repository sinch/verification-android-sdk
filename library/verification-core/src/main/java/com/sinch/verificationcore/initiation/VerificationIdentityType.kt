package com.sinch.verificationcore.initiation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class VerificationIdentityType(val value: String) {
    @SerialName("number")
    NUMBER("number")
}