package com.sinch.verificationcore.initiation.response

import com.sinch.verificationcore.internal.VerificationMethodType

interface InitiationResponseData {
    val id: String
    val method: VerificationMethodType
}