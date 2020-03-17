package com.sinch.smsverification.initialization

import com.sinch.verificationcore.initiation.response.InitiationResponseData
import com.sinch.verificationcore.internal.VerificationMethodType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SmsInitiationResponseData(
    override val id: String,
    @SerialName("sms") val details: SmsInitializationDetails
) : InitiationResponseData {
    override val method: VerificationMethodType = VerificationMethodType.SMS
}