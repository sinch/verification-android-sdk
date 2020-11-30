package com.sinch.verification.all.auto.initialization

import com.sinch.verification.callout.initialization.CalloutInitializationDetails
import com.sinch.verification.core.initiation.response.InitiationDetails
import com.sinch.verification.core.initiation.response.InitiationResponseData
import com.sinch.verification.core.internal.VerificationMethodType
import com.sinch.verification.flashcall.initialization.FlashCallInitializationDetails
import com.sinch.verification.seamless.initialization.SeamlessInitializationDetails
import com.sinch.verification.sms.initialization.SmsInitializationDetails
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Class containing data returned by the API in response to initiation request using [AutoVerificationMethod].
 * @property id Id of the verification.
 * @property smsDetails Detailed data connected to sms verification method if available.
 * @property flashcallDetails Detailed data connected to flashcall verification method if available.
 * @property calloutDetails Detailed data connected to callout verification method if available.
 * @property seamlessDetails Detailed data connected to seamless verification method if available.
 * @property method Method of the verification. Always [VerificationMethodType.AUTO]
 */
@Serializable
data class AutoInitializationResponseData(
    @SerialName("id") override val id: String,
    @SerialName("auto") val autoDetails: AutoInitializationResponseDetails,
    @SerialName("sms") val smsDetails: SmsInitializationDetails? = null,
    @SerialName("flashCall") val flashcallDetails: FlashCallInitializationDetails? = null,
    @SerialName("callout") val calloutDetails: CalloutInitializationDetails? = null,
    @SerialName("seamless") val seamlessDetails: SeamlessInitializationDetails? = null
) : InitiationResponseData {
    @SerialName("method")
    override val method: VerificationMethodType = VerificationMethodType.AUTO

    fun subVerificationDetails(methodType: VerificationMethodType): InitiationDetails? =
        when (methodType) {
            VerificationMethodType.SMS -> smsDetails
            VerificationMethodType.FLASHCALL -> flashcallDetails
            VerificationMethodType.CALLOUT -> calloutDetails
            VerificationMethodType.SEAMLESS -> seamlessDetails
            else -> null
        }
}