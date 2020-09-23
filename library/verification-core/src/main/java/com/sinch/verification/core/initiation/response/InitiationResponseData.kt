package com.sinch.verification.core.initiation.response

import com.sinch.verification.core.internal.VerificationMethodType

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