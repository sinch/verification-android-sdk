package com.sinch.verification.core.verification.response

import com.sinch.verification.core.internal.Verification

/**
 * Listener holding callbacks invoked by the [Verification] implementations notifying about the verification process result.
 */
interface VerificationListener {

    /**
     * Called after entire verification process has finished successfully.
     */
    fun onVerified()

    /**
     * Called when the verification process has finished with an error.
     * @param t Error describing the reason why the process has failed.
     */
    fun onVerificationFailed(t: Throwable)
}