package com.sinch.verificationcore.verification.response

import com.sinch.verificationcore.internal.VerificationMethodType
import com.sinch.verificationcore.internal.VerificationStatus
import com.sinch.verificationcore.verification.VerificationSourceType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VerificationResponseData(
    @SerialName("id") val id: String,
    @SerialName("method") val method: VerificationMethodType,
    @SerialName("source") val source: VerificationSourceType,
    @SerialName("status") val status: VerificationStatus
)