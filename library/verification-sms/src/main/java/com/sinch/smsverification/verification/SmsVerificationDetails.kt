package com.sinch.smsverification.verification

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Class containing detailed information for the actual verification API request.
 * @property code Verification code received in the SMS message.
 */
@Serializable
data class SmsVerificationDetails(@SerialName("code") val code: String)