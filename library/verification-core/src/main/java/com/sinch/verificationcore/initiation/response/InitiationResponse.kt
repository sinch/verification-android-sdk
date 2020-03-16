package com.sinch.verificationcore.initiation.response

import com.sinch.verificationcore.internal.VerificationMethodType

abstract class InitiationResponse (val id: String, val method: VerificationMethodType)