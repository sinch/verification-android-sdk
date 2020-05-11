package com.sinch.verification.callout.verification

import com.sinch.verificationcore.internal.VerificationMethodType
import com.sinch.verificationcore.verification.VerificationData
import com.sinch.verificationcore.verification.VerificationSourceType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Class containing detailed information for the actual verification API request. Note that the user
 * has to manually type the code thus source is always [VerificationSourceType.MANUAL].
 * @property code Code that was passed to the user by text-to-speech call.
 */
@Serializable
data class CalloutVerificationData(
    @SerialName("code") val code: String
) : VerificationData {
    @SerialName("method")
    override val method: VerificationMethodType = VerificationMethodType.CALLOUT

    @SerialName("source")
    override val source: VerificationSourceType = VerificationSourceType.MANUAL
}