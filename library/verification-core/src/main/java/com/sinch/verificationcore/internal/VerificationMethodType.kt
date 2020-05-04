package com.sinch.verificationcore.internal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Enum defining specific type of the verification.
 */
@Serializable
enum class VerificationMethodType(val value: String) {

    /**
     * SMS verification. [More](https://www.sinch.com/products/apis/verification/sms/)
     */
    @SerialName("sms")
    SMS("sms"),

    /**
     * FlashCall verification. [More](https://www.sinch.com/products/apis/verification/flash-call/)
     */
    @SerialName("flashCall")
    FLASHCALL("flashCall")
}