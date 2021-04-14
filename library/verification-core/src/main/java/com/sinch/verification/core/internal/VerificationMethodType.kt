package com.sinch.verification.core.internal

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.*

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
    FLASHCALL("flashCall"),

    /**
     * Callout verification. [More](https://www.sinch.com/products/apis/verification/)
     */
    @SerialName("callout")
    CALLOUT("callout"),

    /**
     * Seamless verification.
     */
    @SerialName("seamless")
    SEAMLESS("seamless"),

    /**
     * Auto verification.
     */
    @SerialName("auto")
    AUTO("auto");

    /**
     * Flag indicating if given method allows typing the verification code manually.
     */
    val allowsManualVerification: Boolean get() = (this != SEAMLESS)

    /**
     * Serializer used to decode [VerificationMethodType] enum. Custom implementation is needed as Sinch API sometimes uses
     * camel case and lower case convention interchangeably.
     */
    @Serializer(forClass = VerificationMethodType::class)
    companion object : KSerializer<VerificationMethodType> {

        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor("VerificationMethodType", PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, value: VerificationMethodType) {
            encoder.encodeString(value.value)
        }

        override fun deserialize(decoder: Decoder): VerificationMethodType =
            when (decoder.decodeString().toLowerCase(Locale.ROOT)) {
                SMS.value.toLowerCase(Locale.ROOT) -> SMS
                FLASHCALL.value.toLowerCase(Locale.ROOT) -> FLASHCALL
                CALLOUT.value.toLowerCase(Locale.ROOT) -> CALLOUT
                SEAMLESS.value.toLowerCase(Locale.ROOT) -> SEAMLESS
                AUTO.value.toLowerCase(Locale.ROOT) -> AUTO
                else -> throw SerializationException("Unknown element ${decoder.decodeString()}")
            }

    }
}