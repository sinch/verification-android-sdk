package com.sinch.verification.all.auto.verification

import com.sinch.verification.core.internal.VerificationMethodType
import com.sinch.verification.core.verification.model.VerificationData
import com.sinch.verification.core.verification.model.VerificationSourceType
import com.sinch.verification.core.verification.model.callout.CalloutVerificationDetails
import com.sinch.verification.core.verification.model.flashcall.FlashCallVerificationDetails
import com.sinch.verification.core.verification.model.sms.SmsVerificationDetails
import kotlinx.serialization.SerialName

data class AutoVerificationData(
    @SerialName("method") override val method: VerificationMethodType,
    @SerialName("source") override val source: VerificationSourceType,
    @SerialName("sms") override val smsDetails: SmsVerificationDetails? = null,
    @SerialName("flashCall") override val flashcallDetails: FlashCallVerificationDetails? = null,
    @SerialName("callout") override val calloutDetails: CalloutVerificationDetails? = null
) : VerificationData(method) {

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