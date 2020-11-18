package com.sinch.verification.core.initiation.response

import com.sinch.verification.core.internal.VerificationMethodType

/**
 * Interface defining properties of specific verification method.
 */
interface InitiationDetails {

    /**
     * Id assigned to each verification method that can be used in case of [VerificationMethodType.AUTO]
     */
    val subVerificationId: String?

}