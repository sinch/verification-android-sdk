package com.sinch.verification.core.initiation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Enum representing different kind of identification methods user of the client app can use.
 */
@Serializable
enum class VerificationIdentityType(val value: String) {

    /**
     * Identification by the phone number.
     */
    @SerialName("number")
    NUMBER("number")

}