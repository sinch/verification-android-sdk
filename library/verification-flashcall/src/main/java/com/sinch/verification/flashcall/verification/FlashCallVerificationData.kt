package com.sinch.verification.flashcall.verification

import com.sinch.verification.core.internal.VerificationMethodType
import com.sinch.verification.core.verification.model.VerificationData
import com.sinch.verification.core.verification.model.VerificationSourceType
import com.sinch.verification.core.verification.model.callout.CalloutVerificationDetails
import com.sinch.verification.core.verification.model.flashcall.FlashCallVerificationDetails
import com.sinch.verification.core.verification.model.sms.SmsVerificationDetails

/**
 * Class containing data that is passed to the backend as the actual code verification check.
 * @property source Source of the verification code.
 * @property flashcallDetails Details of the request.
 * @property method Method of the verification. Always [VerificationMethodType.FLASHCALL]
 */
data class FlashCallVerificationData(
    override val source: VerificationSourceType,
    override val flashcallDetails: FlashCallVerificationDetails
) : VerificationData(VerificationMethodType.FLASHCALL) {

    override val smsDetails: SmsVerificationDetails? = null

    override val calloutDetails: CalloutVerificationDetails? = null

}