package com.sinch.verification.flashcall.report

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FlashCallReportDetails(
    @SerialName("lateCall") val isLateCall: Boolean,
    @SerialName("noCall") val isNoCall: Boolean
)