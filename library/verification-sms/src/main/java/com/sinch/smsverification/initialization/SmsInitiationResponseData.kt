package com.sinch.smsverification.initialization

import com.google.gson.annotations.SerializedName
import com.sinch.verificationcore.initiation.response.InitiationResponseData
import com.sinch.verificationcore.internal.VerificationMethodType

data class SmsInitiationResponseData(
    override val id: String,
    @SerializedName("sms") val details: SmsInitializationDetails
) : InitiationResponseData {
    override val method: VerificationMethodType = VerificationMethodType.SMS
}