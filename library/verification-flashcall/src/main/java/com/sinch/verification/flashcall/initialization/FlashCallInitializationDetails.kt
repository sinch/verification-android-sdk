package com.sinch.verification.flashcall.initialization

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FlashCallInitializationDetails(
    @SerialName("cliFilter") val cliFilter: String,
    @SerialName("interceptionTimeout") val interceptionTimeoutApi: Long?,
    @SerialName("reportTimeout") val reportTimeoutApi: Long?,
    @SerialName("denyCallAfter") val denyCallAfter: Long,
    @SerialName("cli") val cli: String? = null
) {
    companion object {
        const val DEFAULT_INTERCEPTION_TIMEOUT = 15000L //in seconds
    }

    val interceptionTimeout: Long
        get() = (interceptionTimeoutApi?.times(1000) ?: 0).let {
            if (it > 0) it else DEFAULT_INTERCEPTION_TIMEOUT
        }

    val reportTimeout: Long
        get() = maxOf(interceptionTimeout, (reportTimeoutApi?.times(1000) ?: 0))

}