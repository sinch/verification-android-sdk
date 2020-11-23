package com.sinch.verification.core.query.callback

import com.sinch.verification.core.verification.response.VerificationResponseData

/**
 * Callback defining methods invoked after getting verification query result.
 */
interface VerificationInfoCallback {

    /**
     * Method invoked when verification info was successfully queried.
     * @param verificationInfo Information about the queried verification.
     */
    fun onSuccess(verificationInfo: VerificationResponseData)

    /**
     * Error callback invoked when there was an error while querying for verification info.
     * @param t Error data
     */
    fun onError(t: Throwable)
}