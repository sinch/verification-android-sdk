package com.sinch.verificationcore.initiation.response

import com.sinch.verificationcore.internal.VerificationMethodType

/**
 * Interface defining common requirements for each verification method initiation response data.
 */
interface InitiationResponseData {

    /**
     * ID assigned to the verification.
     */
    val id: String

    /**
     * Method of initiated verification.
     */
    val method: VerificationMethodType
}