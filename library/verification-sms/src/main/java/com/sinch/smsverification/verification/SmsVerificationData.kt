package com.sinch.smsverification.verification

import com.sinch.verificationcore.internal.VerificationMethodType
import com.sinch.verificationcore.verification.VerificationData
import com.sinch.verificationcore.verification.VerificationSourceType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SmsVerificationData(
    @SerialName("source") override val source: VerificationSourceType,
    @SerialName("sms") val details: SmsVerificationDetails
) : VerificationData {

    @SerialName("method")
    override val method: VerificationMethodType = VerificationMethodType.SMS
}