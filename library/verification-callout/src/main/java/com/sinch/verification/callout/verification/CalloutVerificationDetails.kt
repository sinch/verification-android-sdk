package com.sinch.verification.callout.verification

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
/**
 * Class containing detailed information for the actual verification API request.
 * @property code Code that was passed to the user by text-to-speech call.
 */
data class CalloutVerificationDetails(@SerialName("code") val code: String)