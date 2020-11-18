package com.sinch.verification.all.auto.initialization

import com.sinch.metadata.model.PhoneMetadata
import com.sinch.verification.core.initiation.VerificationIdentity
import com.sinch.verification.core.initiation.VerificationInitiationData
import com.sinch.verification.core.internal.VerificationMethodType
import com.sinch.verification.sms.initialization.SmsOptions
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AutoInitializationData(
    @SerialName("identity") override val identity: VerificationIdentity,
    @SerialName("honourEarlyReject") override val honourEarlyReject: Boolean,
    @SerialName("custom") override val custom: String?,
    @SerialName("reference") override val reference: String?,
    @SerialName("metadata") override val metadata: PhoneMetadata?,
    @SerialName("smsOptions") val smsOptions: SmsOptions
) : VerificationInitiationData {
    @SerialName("method")
    override val method: VerificationMethodType = VerificationMethodType.AUTO
}