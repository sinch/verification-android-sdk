package com.sinch.metadata.model.sim

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SimMetadata(
    @SerialName("countryId") val countryId: String,
    @SerialName("mcc") val mcc: String,
    @SerialName("mnc") val mnc: String
)