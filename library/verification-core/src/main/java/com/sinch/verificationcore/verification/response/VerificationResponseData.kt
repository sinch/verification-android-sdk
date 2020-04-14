package com.sinch.verificationcore.verification.response

import com.sinch.verificationcore.internal.VerificationStatus
import com.sinch.verificationcore.verification.VerificationSourceType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VerificationResponseData(
    @SerialName("id") val id: String,
    @SerialName("source") val source: VerificationSourceType? = null,
    @SerialName("status") val status: VerificationStatus,
    @SerialName("reason") val errorReason: String? = null
)