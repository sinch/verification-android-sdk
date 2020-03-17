package com.sinch.verificationcore.internal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class VerificationMethodType(val value: String) {
    @SerialName("sms")
    SMS("sms")
}