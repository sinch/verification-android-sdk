package com.sinch.metadata.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Class holding general metadata of the device.
 * @property model Model of the device.
 * @property idName Name of the industrial design. [See](https://developer.android.com/reference/android/os/Build#DEVICE)
 * @property manufacturer Name of the manufacturer of the device.
 */
@Serializable
data class DeviceMetadata(
    @SerialName("model") val model: String,
    @SerialName("idname") val idName: String,
    @SerialName("manufacturer") val manufacturer: String
)