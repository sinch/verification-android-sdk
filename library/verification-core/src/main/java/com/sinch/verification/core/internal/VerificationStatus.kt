@file:Suppress("unused")

package com.sinch.verification.core.internal

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.*

/**
 * Enum describing API status of the verification.
 */
@Serializable
enum class VerificationStatus(val value: String) {

    /**
     * Verification is ongoing.
     */
    @SerialName("PENDING")
    PENDING("PENDING"),

    /**
     * Verification has been successfully processed.
     */
    @SerialName("SUCCESSFUL")
    SUCCESSFUL("SUCCESSFUL"),

    /**
     * Verification attempt was made, but the number was not verified.
     */
    @SerialName("FAIL")
    FAILED("FAIL"),

    /**
     * Verification attempt was denied by Sinch or your backend.
     */
    @SerialName("DENIED")
    DENIED("DENIED"),

    /**
     * Verification attempt was aborted.
     */
    @SerialName("ABORTED")
    ABORTED("ABORTED"),

    /**
     * Verification attempt could not be completed due to a network error or the number being unreachable.
     */
    @SerialName("ERROR")
    ERROR("ERROR");

    /**
     * Serializer used to decode [VerificationStatus] enum. Custom implementation is needed as Sinch API sometimes uses
     * camel case and lower case convention interchangeably.
     */
    @Serializer(forClass = VerificationStatus::class)
    companion object : KSerializer<VerificationStatus> {

        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor("VerificationStatus", PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, value: VerificationStatus) {
            encoder.encodeString(value.value)
        }

        override fun deserialize(decoder: Decoder): VerificationStatus =
            when (decoder.decodeString().toLowerCase(Locale.ROOT)) {
                PENDING.value.toLowerCase(Locale.ROOT) -> PENDING
                SUCCESSFUL.value.toLowerCase(Locale.ROOT) -> SUCCESSFUL
                FAILED.value.toLowerCase(Locale.ROOT) -> FAILED
                DENIED.value.toLowerCase(Locale.ROOT) -> DENIED
                ABORTED.value.toLowerCase(Locale.ROOT) -> ABORTED
                ERROR.value.toLowerCase(Locale.ROOT) -> ERROR
                else -> throw SerializationException("Unknown element ${decoder.decodeString()}")
            }

    }
}