package com.sinch.verificationcore.initiation

import com.sinch.verificationcore.initiation.metadata.PhoneMetadata
import com.sinch.verificationcore.internal.VerificationMethodType

abstract class VerificationInitiationData(
    val method: VerificationMethodType,
    val identity: VerificationIdentity,
    val honourEarlyReject: Boolean,
    val custom: String?,
    val metadata: PhoneMetadata? = null
)