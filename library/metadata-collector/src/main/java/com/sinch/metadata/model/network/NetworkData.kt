package com.sinch.metadata.model.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkData(@SerialName("type") val type: NetworkType)