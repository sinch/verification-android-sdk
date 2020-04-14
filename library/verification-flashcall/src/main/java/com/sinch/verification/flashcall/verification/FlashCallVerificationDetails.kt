package com.sinch.verification.flashcall.verification

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FlashCallVerificationDetails(@SerialName("cli") val cli: String)