package com.sinch.metadata.model.sim

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OperatorInfo(
    @SerialName("countryId") val countryId: String,
    @SerialName("name") val name: String,
    @SerialName("isRoaming") val isRoaming: Boolean,
    @SerialName("mcc") val mcc: String?,
    @SerialName("mnc") val mnc: String?
)