package com.sinch.smsverification.initialization

import com.sinch.verificationcore.initiation.VerificationIdentity
import com.sinch.verificationcore.initiation.VerificationInitiationData
import com.sinch.verificationcore.internal.VerificationMethodType
import kotlinx.serialization.Serializable

@Serializable
class SmsVerificationInitiationData(
    override val identity: VerificationIdentity,
    override val honourEarlyReject: Boolean,
    override val custom: String?
) : VerificationInitiationData {
    override val method: VerificationMethodType = VerificationMethodType.SMS
}