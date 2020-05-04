package com.sinch.verificationcore.internal.error

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Class containing detailed information about what went wrong during the API call. (Server did not return 2xx status).
 * @property errorCode Integer defining specific error.
 * @property message Human readable message describing why API call has failed
 * @property reference Optional reference id that was passed with the request.
 */
@Serializable
data class ApiErrorData(
    @SerialName("errorCode") val errorCode: Int? = null,
    @SerialName("message") val message: String? = null,
    @SerialName("reference") val reference: String? = null
)