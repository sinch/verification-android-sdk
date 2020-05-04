package com.sinch.verification.flashcall.verification

import com.sinch.verificationcore.internal.VerificationMethodType
import com.sinch.verificationcore.verification.VerificationData
import com.sinch.verificationcore.verification.VerificationSourceType
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