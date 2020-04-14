package com.sinch.verification.flashcall.initialization

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FlashCallInitializationDetails(
    @SerialName("cliFilter") val cliFilter: String,
    @SerialName("interceptionTimeout") val interceptionTimeout: Long,
    @SerialName("reportTimeout") val reportTimeout: Long,
    @SerialName("denyCallAfter") val denyCallAfter: Long,
    @SerialName("cli") val cli: String? = null
)