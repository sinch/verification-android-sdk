package com.sinch.verificationcore.internal.error

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiErrorData(
    @SerialName("errorCode") val errorCode: Int,
    @SerialName("message") val message: String,
    @SerialName("reference") val reference: String
)