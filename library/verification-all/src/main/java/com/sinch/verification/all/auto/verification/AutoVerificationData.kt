package com.sinch.verification.all.auto.verification

import com.sinch.verification.callout.verification.CalloutVerificationDetails
import com.sinch.verification.core.internal.VerificationMethodType
import com.sinch.verification.core.verification.VerificationData
import com.sinch.verification.core.verification.VerificationSourceType
import com.sinch.verification.flashcall.verification.FlashCallVerificationDetails
import com.sinch.verification.sms.verification.SmsVerificationDetails
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AutoVerificationData(
    @SerialName("method") override val method: VerificationMethodType,
    @SerialName("source") override val source: VerificationSourceType,
    @SerialName("sms") val smsDetails: SmsVerificationDetails? = null,
    @SerialName("flashCall") val flashcallDetails: FlashCallVerificationDetails? = null,
    @SerialName("callout") val calloutDetails: CalloutVerificationDetails? = null
) : VerificationData {

    companion object {

        fun create(
            method: VerificationMethodType,
            source: VerificationSourceType,
            code: String
        ): AutoVerificationData = when (method) {
            VerificationMethodType.SMS -> AutoVerificationData(
                method = method,
                source = source,
                smsDetails = SmsVerificationDetails(code = code)
            )
            VerificationMethodType.FLASHCALL -> AutoVerificationData(
                method = method,
                source = source,
                flashcallDetails = FlashCallVerificationDetails(cli = code)
            )
            VerificationMethodType.CALLOUT -> AutoVerificationData(
                method = method,
                source = source,
                calloutDetails = CalloutVerificationDetails(code = code)
            )
            else -> error("Cannot construct verification data for method $method)")
        }

    }

}