package com.sinch.verification.seamless.initialization

import com.sinch.verification.seamless.SeamlessVerificationMethod
import com.sinch.verification.core.initiation.response.InitiationResponseData
import com.sinch.verification.core.internal.VerificationMethodType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Class containing data returned by the API in response to initiation request using [SeamlessVerificationMethod].
 * @property id Id of the verification.
 * @property details Details of the initiated seamless verification process.
 * @property method Method of the verification. Always [VerificationMethodType.SEAMLESS]
 */
@Serializable
data class SeamlessInitiationResponseData(
    @SerialName("id") override val id: String,
    @SerialName("seamless") val details: SeamlessInitializationDetails
) : InitiationResponseData {
    @SerialName("method")
    override val method: VerificationMethodType = VerificationMethodType.SEAMLESS
}