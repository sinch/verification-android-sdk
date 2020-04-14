package com.sinch.smsverification.initialization

import com.sinch.verificationcore.initiation.response.InitiationResponseData
import com.sinch.verificationcore.internal.VerificationMethodType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SmsInitiationResponseData(
    @SerialName("id") override val id: String,
    @SerialName("sms") val details: SmsInitializationDetails,
    @Transient val contentLanguage: String = ""
) : InitiationResponseData {
    @SerialName("method")
    override val method: VerificationMethodType = VerificationMethodType.SMS
}