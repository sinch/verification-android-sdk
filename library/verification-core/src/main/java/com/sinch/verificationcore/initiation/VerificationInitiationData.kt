package com.sinch.verificationcore.initiation

import com.sinch.metadata.model.PhoneMetadata
import com.sinch.verificationcore.internal.VerificationMethodType

/**
 * Common interface defining requirements of every initiation data different verification methods use.
 */
interface VerificationInitiationData {

    /**
     * Method of the verification.
     */
    val method: VerificationMethodType

    /**
     * Identity of the verification.
     */
    val identity: VerificationIdentity

    /**
     * Flag indicating if verification process should use early rejection rules.
     */
    val honourEarlyReject: Boolean

    /**
     * Custom string passed in the initiation API call.
     */
    val custom: String?

    /**
     * Custom string that can be passed in the request for tracking purposes.
     */
    val reference: String?

    /**
     * Metadata about the device.
     */
    val metadata: PhoneMetadata?
}