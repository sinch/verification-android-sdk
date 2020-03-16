package com.sinch.smsverification.initialization

import com.sinch.verificationcore.initiation.VerificationIdentity
import com.sinch.verificationcore.initiation.VerificationInitiationData
import com.sinch.verificationcore.initiation.metadata.PhoneMetadata
import com.sinch.verificationcore.internal.VerificationMethodType

class SmsVerificationInitiationData(
    identity: VerificationIdentity,
    honourEarlyReject: Boolean,
    metadata: PhoneMetadata? = null
) : VerificationInitiationData(VerificationMethodType.SMS, identity, honourEarlyReject, metadata)