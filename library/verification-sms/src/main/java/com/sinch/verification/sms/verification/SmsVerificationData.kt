package com.sinch.verification.sms.verification

import com.sinch.verification.core.internal.VerificationMethodType
import com.sinch.verification.core.verification.model.VerificationData
import com.sinch.verification.core.verification.model.VerificationSourceType
import com.sinch.verification.core.verification.model.callout.CalloutVerificationDetails
import com.sinch.verification.core.verification.model.flashcall.FlashCallVerificationDetails
import com.sinch.verification.core.verification.model.sms.SmsVerificationDetails

/**
 * Class containing data that is passed to the backend as the actual code verification check.
 * @property source Source of the verification code.
 * @property smsDetails Details of the request.
 * @property method Method of the verification. Always [VerificationMethodType.SMS]
 */
data class SmsVerificationData(
    override val source: VerificationSourceType,
    override val smsDetails: SmsVerificationDetails
) : VerificationData(VerificationMethodType.SMS) {

    override val flashcallDetails: FlashCallVerificationDetails? = null

    override val calloutDetails: CalloutVerificationDetails? = null

}