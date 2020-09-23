package com.sinch.verification.flashcall.verification

import com.sinch.verification.core.internal.VerificationMethodType
import com.sinch.verification.core.verification.VerificationData
import com.sinch.verification.core.verification.VerificationSourceType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Class containing data that is passed to the backend as the actual code verification check.
 * @property source Source of the verification code.
 * @property details Details of the request.
 * @property method Method of the verification. Always [VerificationMethodType.FLASHCALL]
 */
@Serializable
data class FlashCallVerificationData(
    @SerialName("source") override val source: VerificationSourceType,
    @SerialName("flashCall") val details: FlashCallVerificationDetails
) : VerificationData {
    @SerialName("method")
    override val method: VerificationMethodType = VerificationMethodType.FLASHCALL
}