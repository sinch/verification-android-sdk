package com.sinch.verificationcore.internal.error.earlyreject

import kotlinx.serialization.Serializable

@Serializable
data class EarlyRejectData(val message: String)