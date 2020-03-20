package com.sinch.metadata.model.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkInfo(
    @SerialName("isVoiceCapable") val isVoiceCapable: Boolean?,
    @SerialName("data") val networkData: NetworkData?
)