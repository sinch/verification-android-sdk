package com.sinch.verification.flashcall.initialization

import com.sinch.verificationcore.initiation.response.InitiationResponseData
import com.sinch.verificationcore.internal.VerificationMethodType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class FlashCallInitializationResponseData(
    @SerialName("id") override val id: String,
    @SerialName("flashCall") val details: FlashCallInitializationDetails
) : InitiationResponseData {
    @Transient
    override val method: VerificationMethodType = VerificationMethodType.FLASHCALL
}