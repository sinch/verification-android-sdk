package com.sinch.smsverification.initialization

import com.sinch.verificationcore.initiation.response.InitiationResponseData
import com.sinch.verificationcore.internal.VerificationMethodType

data class SmsInitiationResponseData(override val id: String) : InitiationResponseData {
    override val method: VerificationMethodType = VerificationMethodType.SMS
}