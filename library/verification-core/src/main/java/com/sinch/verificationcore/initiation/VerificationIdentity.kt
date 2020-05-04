package com.sinch.verificationcore.initiation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Class holding information about how user of the client app is identified.
 * @property endpoint String identifying the user. Currently it is always user's phone number.
 * @property type Meaning of the [endpoint] property. Currently only [VerificationIdentityType.NUMBER] is used.
 */
@Serializable
data class VerificationIdentity(
    @SerialName("endpoint") val endpoint: String,
    @SerialName("type") val type: VerificationIdentityType = VerificationIdentityType.NUMBER
)