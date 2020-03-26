package com.sinch.verificationcore.verification

import com.sinch.verificationcore.internal.VerificationMethodType

interface VerificationData {
    val method: VerificationMethodType
    val source: VerificationSourceType
}