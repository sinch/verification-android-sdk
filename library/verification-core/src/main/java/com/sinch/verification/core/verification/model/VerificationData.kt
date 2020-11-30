package com.sinch.verification.core.verification.model

import com.sinch.verification.core.internal.VerificationMethodType
import com.sinch.verification.core.verification.model.callout.CalloutVerificationDetails
import com.sinch.verification.core.verification.model.flashcall.FlashCallVerificationDetails
import com.sinch.verification.core.verification.model.sms.SmsVerificationDetails
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Common fields of verification data that is passed to the backend with the verification request.
 */
@Serializable
open class VerificationData(@SerialName("method") open val method: VerificationMethodType) {

    /**
     * Source of the verification code.
     */
    @SerialName("source")
    open val source: VerificationSourceType = VerificationSourceType.MANUAL

    /**
     * Details of the verification request needed for sms method. Null otherwise.
     */
    @SerialName("sms")
    open val smsDetails: SmsVerificationDetails? = null

    /**
     * Details of the verification request needed for flashcall method. Null otherwise.
     */
    @SerialName("flashcall")
    open val flashcallDetails: FlashCallVerificationDetails? = null

    /**
     * Details of the verification request needed for callout method. Null otherwise.
     */
    @SerialName("callout")
    open val calloutDetails: CalloutVerificationDetails? = null
}