package com.sinch.verificationcore.initiation

import kotlinx.serialization.Serializable

@Serializable
data class VerificationIdentity(
    val endpoint: String,
    val type: VerificationIdentityType = VerificationIdentityType.NUMBER
)