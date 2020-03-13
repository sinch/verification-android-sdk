package com.sinch.smsverification

import com.sinch.verificationcore.request.VerificationIdentity
import com.sinch.verificationcore.request.VerificationRequestData
import com.sinch.verificationcore.request.metadata.PhoneMetadata

class SmsVerificationRequestData(
    identity: VerificationIdentity,
    honourEarlyReject: Boolean,
    metadata: PhoneMetadata? = null
) : VerificationRequestData("sms", identity, honourEarlyReject, metadata)