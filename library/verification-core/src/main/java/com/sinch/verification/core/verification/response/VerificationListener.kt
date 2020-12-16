package com.sinch.verification.core.verification.response

import com.sinch.verification.core.internal.Verification
import com.sinch.verification.core.verification.VerificationEvent

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

    /**
     * Called when the verification process completed a certain step or wants to pass debug information to the client.
     * For specific events and usage see verification method specific documentation.
     * @param event Data containing information about the verification process.
     */
    fun onVerificationEvent(event: VerificationEvent) {}
}