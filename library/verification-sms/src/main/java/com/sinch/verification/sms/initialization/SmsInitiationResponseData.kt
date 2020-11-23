package com.sinch.verification.sms.initialization

import com.sinch.verification.sms.SmsVerificationMethod
import com.sinch.verification.core.initiation.response.InitiationResponseData
import com.sinch.verification.core.internal.VerificationMethodType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Class containing data returned by the API in response to initiation request using [SmsVerificationMethod].
 * @property id Id of the verification.
 * @property details Details of the initiated sms verification process.
 * @property contentLanguage Language of sms message that will be sent.
 * @property method Method of the verification. Always [VerificationMethodType.SMS]
 */
@Serializable
data class SmsInitiationResponseData(
    @SerialName("id") override val id: String,
    @SerialName("sms") val details: SmsInitializationDetails,
    @Transient val contentLanguage: String = ""
) : InitiationResponseData {
    @SerialName("method")
    override val method: VerificationMethodType = VerificationMethodType.SMS
}