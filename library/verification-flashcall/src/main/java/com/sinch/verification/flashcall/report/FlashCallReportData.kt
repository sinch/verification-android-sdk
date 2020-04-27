package com.sinch.verification.flashcall.report

import com.sinch.verificationcore.internal.VerificationMethodType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FlashCallReportData(@SerialName("data") val details: FlashCallReportDetails) {
    @SerialName("method")
    val method: VerificationMethodType = VerificationMethodType.FLASHCALL
}