package com.sinch.verification.seamless

/**
 * Strategy encapsulating the actual verification step of the seamless flow. The protocol version
 * returned by the initiation call decides which implementation is used:
 * - v1 (or missing version) -> [SeamlessVerificationMethod.CellularRedirectStrategy] which performs
 *   a GET on the returned `targetUri` forced over the mobile network.
 * - v2 -> [com.sinch.verification.seamless.digitalcredential.DigitalCredentialVerification]
 *   which presents a digital credential request via the Android Credential Manager and reports it,
 *   or [AppUrlVerificationStrategy] which delegates to the carrier app.
 */
interface SeamlessVerificationStrategy {

    /**
     * Starts the verification flow.
     * @param payload Version specific payload carried through the verification channel:
     * the `targetUri` for v1, the `vpResponse` request JSON for the credential flow, or the
     * `androidAppUrl` for the carrier app flow.
     */
    fun start(payload: String)

    /** Handles a callback delivered after startup; strategies without callbacks need not override this. */
    fun handleCallback(callbackUrl: String) = Unit

    /**
     * Cancels any ongoing work started by [start].
     */
    fun stop()
}
