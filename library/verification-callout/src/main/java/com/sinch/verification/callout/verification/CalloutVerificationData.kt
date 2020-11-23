package com.sinch.verification.callout.verification

import com.sinch.verification.core.internal.VerificationMethodType
import com.sinch.verification.core.verification.VerificationData
import com.sinch.verification.core.verification.VerificationSourceType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Class containing detailed information for the actual verification API request. Note that the user
 * has to manually type the code thus source is always [VerificationSourceType.MANUAL].
 * @property details Details of the request.
 */
@Serializable
data class CalloutVerificationData(
    @SerialName("callout") val details: CalloutVerificationDetails
) : VerificationData {
    @SerialName("method")
    override val method: VerificationMethodType = VerificationMethodType.CALLOUT

    @SerialName("source")
    override val source: VerificationSourceType = VerificationSourceType.MANUAL
}