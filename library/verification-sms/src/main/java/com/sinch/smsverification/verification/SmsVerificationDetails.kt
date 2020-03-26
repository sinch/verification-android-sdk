package com.sinch.smsverification.verification

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SmsVerificationDetails(@SerialName("code") val code: String)