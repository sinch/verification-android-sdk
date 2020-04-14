package com.sinch.verification.flashcall.verification

import com.sinch.verificationcore.internal.VerificationMethodType
import com.sinch.verificationcore.verification.VerificationData
import com.sinch.verificationcore.verification.VerificationSourceType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FlashCallVerificationData(
    @SerialName("source") override val source: VerificationSourceType,
    @SerialName("flashCall") val details: FlashCallVerificationDetails
) : VerificationData {
    @SerialName("method")
    override val method: VerificationMethodType = VerificationMethodType.FLASHCALL
}