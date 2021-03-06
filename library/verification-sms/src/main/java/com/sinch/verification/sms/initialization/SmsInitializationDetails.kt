package com.sinch.verification.sms.initialization

import com.sinch.verification.core.initiation.response.InitiationDetails
import com.sinch.verification.core.internal.VerificationMethodType
import com.sinch.verification.core.verification.interceptor.CodeInterceptionTimeoutException
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Class containing details (returned by the API) about the initiated sms verification process.
 * @property template Template defining the place where the verification code is embedded in the sms message.
 * @property interceptionTimeout Maximum timeout after which interceptor should report [CodeInterceptionTimeoutException].
 * @property subVerificationId Id assigned to each verification method that can be used in case of [VerificationMethodType.AUTO]
 */
@Serializable
data class SmsInitializationDetails(
    @SerialName("template") val template: String,
    @SerialName("interceptionTimeout") val interceptionTimeoutApi: Long,
    @SerialName("subVerificationId") override val subVerificationId: String? = null
) : InitiationDetails {
    /**
     * Interception timeout to be used by the SDK.
     */
    val interceptionTimeout: Long
        get() = interceptionTimeoutApi.times(1000)
}