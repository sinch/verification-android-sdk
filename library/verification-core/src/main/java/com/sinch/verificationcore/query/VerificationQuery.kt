package com.sinch.verificationcore.query

import com.sinch.verificationcore.internal.VerificationMethodType
import com.sinch.verificationcore.query.callback.VerificationInfoCallback

/**
 * Interface defining methods of getting verification information based on different properties.
 */
interface VerificationQuery {

    /**
     * Queries for verification information based on verification id.
     * @param id Id of the verification to be queried.
     * @param callback Callback invoked after verification information has been queried.
     */
    fun queryById(id: String, callback: VerificationInfoCallback)

    /**
     * Queries for verification information based on verification reference.
     * @param reference Reference field of the verification to be queried.
     * @param callback Callback invoked after verification information has been queried.
     */
    fun queryByReference(reference: String, callback: VerificationInfoCallback)

    /**
     * Queries for verification based on number and method used for the verification.
     * NOTE: This query is valid only for a limited time after the verification process has started.
     * @param method [VerificationMethodType] of the verification.
     * @param number Phone number that was verified.
     * @param callback Callback invoked after verification information has been queried.
     */
    fun queryByEndpoint(
        method: VerificationMethodType,
        number: String,
        callback: VerificationInfoCallback
    )
}