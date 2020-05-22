package com.sinch.verification.callout.initialization

import com.sinch.metadata.model.PhoneMetadata
import com.sinch.verificationcore.initiation.VerificationIdentity
import com.sinch.verificationcore.initiation.VerificationInitiationData
import com.sinch.verificationcore.internal.VerificationMethodType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Class containing data that is passed with the callout verification initiation API call.
 * @property identity Identity of the verification.
 * @property honourEarlyReject Flag indicating if verification process should use early rejection rules.
 * @property custom Custom string passed in the initiation API call.
 * @property reference Custom string that can be passed in the request for tracking purposes.
 * @property metadata Metadata containing information about the device used for analytics and early rejection rules.
 * @property method Method of the verification. Always [VerificationMethodType.CALLOUT]
 */
@Serializable
data class CalloutVerificationInitializationData(
    @SerialName("identity") override val identity: VerificationIdentity,
    @SerialName("honourEarlyReject") override val honourEarlyReject: Boolean,
    @SerialName("custom") override val custom: String?,
    @SerialName("reference") override val reference: String?,
    @SerialName("metadata") override val metadata: PhoneMetadata?
) : VerificationInitiationData {
    @SerialName("method")
    override val method: VerificationMethodType = VerificationMethodType.CALLOUT
}