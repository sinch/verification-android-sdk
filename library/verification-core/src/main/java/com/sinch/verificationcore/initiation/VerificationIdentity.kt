package com.sinch.verificationcore.initiation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VerificationIdentity(
    @SerialName("endpoint") val endpoint: String,
    @SerialName("type") val type: VerificationIdentityType = VerificationIdentityType.NUMBER
)