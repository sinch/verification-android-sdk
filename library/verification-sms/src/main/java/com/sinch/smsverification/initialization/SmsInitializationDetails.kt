package com.sinch.smsverification.initialization

import com.sinch.verificationcore.verification.interceptor.CodeInterceptionTimeoutException
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Class containing details (returned by the API) about the initiated sms verification process.
 * @property template Template defining the place where the verification code is embedded in the sms message.
 * @property interceptionTimeout Maximum timeout after which interceptor should report [CodeInterceptionTimeoutException].
 */
@Serializable
data class SmsInitializationDetails(
    @SerialName("template") val template: String,
    @SerialName("interceptionTimeout") val interceptionTimeoutApi: Long
) {
    /**
     * Interception timeout to be used by the SDK.
     */
    val interceptionTimeout: Long
        get() = interceptionTimeoutApi.times(1000)
}