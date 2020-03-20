package com.sinch.metadata.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeviceMetadata(
    @SerialName("model") val model: String,
    @SerialName("idname") val idName: String,
    @SerialName("manufacturer") val manufacturer: String
)