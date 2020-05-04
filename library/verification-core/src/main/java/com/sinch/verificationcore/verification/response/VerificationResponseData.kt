package com.sinch.verificationcore.verification.response

import com.sinch.verificationcore.internal.VerificationStatus
import com.sinch.verificationcore.verification.VerificationSourceType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Class holding data passed by the backend in response to verification request.
 * @param id Id of the verification process.
 * @param source Source of the verification.
 * @param status Status of the verification.
 * @param errorReason Error message if the verification process has failed, null otherwise.
 */
@Serializable
data class VerificationResponseData(
    @SerialName("id") val id: String,
    @SerialName("source") val source: VerificationSourceType? = null,
    @SerialName("status") val status: VerificationStatus,
    @SerialName("reason") val errorReason: String? = null
)