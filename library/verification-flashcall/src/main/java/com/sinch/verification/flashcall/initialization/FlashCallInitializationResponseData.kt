package com.sinch.verification.flashcall.initialization

import com.sinch.verification.flashcall.FlashCallVerificationMethod
import com.sinch.verificationcore.initiation.response.InitiationResponseData
import com.sinch.verificationcore.internal.VerificationMethodType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Class containing data returned by the API in response to initiation request using [FlashCallVerificationMethod].
 * @property id Id of the verification.
 * @property details Details of the initiated flashcall verification process.
 * @property method Method of the verification. Always [VerificationMethodType.FLASHCALL]
 */
@Serializable
data class FlashCallInitializationResponseData(
    @SerialName("id") override val id: String,
    @SerialName("flashCall") val details: FlashCallInitializationDetails
) : InitiationResponseData {
    @Transient
    override val method: VerificationMethodType = VerificationMethodType.FLASHCALL
}