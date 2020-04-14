package com.sinch.verification.flashcall.initialization

import com.sinch.metadata.model.PhoneMetadata
import com.sinch.verificationcore.initiation.VerificationIdentity
import com.sinch.verificationcore.initiation.VerificationInitiationData
import com.sinch.verificationcore.internal.VerificationMethodType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FlashCallVerificationInitializationData(
    @SerialName("identity") override val identity: VerificationIdentity,
    @SerialName("honourEarlyReject") override val honourEarlyReject: Boolean,
    @SerialName("custom") override val custom: String?,
    @SerialName("metadata") override val metadata: PhoneMetadata?
) : VerificationInitiationData {
    @SerialName("method")
    override val method: VerificationMethodType = VerificationMethodType.FLASHCALL
}