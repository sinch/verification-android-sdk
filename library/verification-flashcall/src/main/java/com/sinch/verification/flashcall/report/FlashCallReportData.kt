package com.sinch.verification.flashcall.report

import com.sinch.verification.flashcall.FlashCallVerificationService
import com.sinch.verification.core.internal.VerificationMethodType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Class containing data that is passed to [FlashCallVerificationService.reportVerification] API
 * API endpoint, containing analytics information about the verification process.
 * @property details Details of finished verification process.
 * @property method Method of the verification. Here always [VerificationMethodType.FLASHCALL].
 */
@Serializable
data class FlashCallReportData(@SerialName("data") val details: FlashCallReportDetails) {
    @SerialName("method")
    val method: VerificationMethodType = VerificationMethodType.FLASHCALL
}