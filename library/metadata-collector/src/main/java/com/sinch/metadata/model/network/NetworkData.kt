package com.sinch.metadata.model.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Class holding metadata about currently connected data network.
 * @property type Type of data network.
 */
@Serializable
data class NetworkData(@SerialName("type") val type: NetworkType)