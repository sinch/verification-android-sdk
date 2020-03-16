package com.sinch.verificationcore.initiation

data class VerificationIdentity(val endpoint: String, val type: VerificationIdentityType = VerificationIdentityType.NUMBER)