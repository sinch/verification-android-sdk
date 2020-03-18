package com.sinch.smsverification.initialization

import kotlinx.serialization.Serializable

@Serializable
data class SmsInitializationDetails(val template: String, val interceptionTimeout: Int)