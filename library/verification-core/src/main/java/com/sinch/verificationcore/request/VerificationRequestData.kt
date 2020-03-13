package com.sinch.verificationcore.request

import com.sinch.verificationcore.request.metadata.PhoneMetadata

abstract class VerificationRequestData(
    val method: String,
    val identity: VerificationIdentity,
    val honourEarlyReject: Boolean,
    val metadata: PhoneMetadata? = null
)