package com.sinch.verification.callout.verification

import com.sinch.verification.core.internal.VerificationMethodType
import com.sinch.verification.core.verification.model.VerificationData
import com.sinch.verification.core.verification.model.VerificationSourceType
import com.sinch.verification.core.verification.model.callout.CalloutVerificationDetails
import com.sinch.verification.core.verification.model.flashcall.FlashCallVerificationDetails
import com.sinch.verification.core.verification.model.sms.SmsVerificationDetails

/**
 * Class containing detailed information for the actual verification API request. Note that the user
 * has to manually type the code thus source is always [VerificationSourceType.MANUAL].
 * @property calloutDetails Details of the request.
 */
data class CalloutVerificationData(
    override val calloutDetails: CalloutVerificationDetails
) : VerificationData(VerificationMethodType.CALLOUT) {

    override val source: VerificationSourceType = VerificationSourceType.MANUAL

    override val smsDetails: SmsVerificationDetails? = null

    override val flashcallDetails: FlashCallVerificationDetails? = null

}