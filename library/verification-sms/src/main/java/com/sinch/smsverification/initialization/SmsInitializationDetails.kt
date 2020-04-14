package com.sinch.smsverification.initialization

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SmsInitializationDetails(
    @SerialName("template") val template: String,
    @SerialName("interceptionTimeout") val interceptionTimeout: Long
)