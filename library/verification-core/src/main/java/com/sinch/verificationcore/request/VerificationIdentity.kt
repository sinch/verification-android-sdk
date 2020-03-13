package com.sinch.verificationcore.request

data class VerificationIdentity(val endpoint: String, val type: String = "number")