package com.sinch.verification.callout.initialization

import com.sinch.verification.core.initiation.response.InitiationDetails
import com.sinch.verification.core.internal.VerificationMethodType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Class containing details (returned by the API) about the initiated callout verification process.
 * @property subVerificationId Id assigned to each verification method that can be used in case of [VerificationMethodType.AUTO]
 */
@Serializable
data class CalloutInitializationDetails(
    @SerialName("subVerificationId") override val subVerificationId: String? = null
) : InitiationDetails