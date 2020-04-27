package com.sinch.verificationcore.internal.error

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiErrorData(
    @SerialName("errorCode") val errorCode: Int? = null,
    @SerialName("message") val message: String? = null,
    @SerialName("reference") val reference: String? = null
)