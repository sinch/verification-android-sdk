package com.sinch.smsverification.initialization

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SmsOptions(@SerialName("applicationHash") val applicationHash: String? = null)