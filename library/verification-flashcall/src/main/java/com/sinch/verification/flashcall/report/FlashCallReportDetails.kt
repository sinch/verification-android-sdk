package com.sinch.verification.flashcall.report

import com.sinch.verification.flashcall.FlashCallVerificationService
import com.sinch.verification.flashcall.initialization.FlashCallInitializationDetails
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Class containing analytics information that is passed to report API endpoint
 * [FlashCallVerificationService.reportVerification].
 * @property isLateCall Flag indicating if the call was received after [FlashCallInitializationDetails.interceptionTimeout]
 * but before [FlashCallInitializationDetails.reportTimeout]
 * @property isNoCall Flag indicating if the call was received at all.
 */
@Serializable
data class FlashCallReportDetails(
    @SerialName("lateCall") val isLateCall: Boolean,
    @SerialName("noCall") val isNoCall: Boolean
)