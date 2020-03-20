package com.sinch.smsverification.initialization

import com.sinch.metadata.model.PhoneMetadata
import com.sinch.verificationcore.initiation.VerificationIdentity
import com.sinch.verificationcore.initiation.VerificationInitiationData
import com.sinch.verificationcore.internal.VerificationMethodType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class SmsVerificationInitiationData(
    @SerialName("identity") override val identity: VerificationIdentity,
    @SerialName("honourEarlyReject") override val honourEarlyReject: Boolean,
    @SerialName("custom") override val custom: String?,
    @SerialName("metadata") override val metadata: PhoneMetadata?
) : VerificationInitiationData {
    @SerialName("method")
    override val method: VerificationMethodType = VerificationMethodType.SMS
}