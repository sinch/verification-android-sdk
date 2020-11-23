package com.sinch.verification.callout.initialization

import com.sinch.verification.callout.CalloutVerificationMethod
import com.sinch.verification.core.initiation.response.InitiationResponseData
import com.sinch.verification.core.internal.VerificationMethodType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Class containing data returned by the API in response to initiation request using [CalloutVerificationMethod].
 * @property id Id of the verification.
 * @property method Method of the verification. Always [VerificationMethodType.CALLOUT]
 */
@Serializable
class CalloutInitializationResponseData(
    @SerialName("id") override val id: String
) : InitiationResponseData {
    @Transient
    override val method: VerificationMethodType = VerificationMethodType.CALLOUT
}