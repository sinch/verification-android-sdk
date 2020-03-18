package com.sinch.verificationcore.internal.error

import kotlinx.serialization.Serializable

@Serializable
data class ApiErrorData(val errorCode: Int, val message: String, val reference: String)