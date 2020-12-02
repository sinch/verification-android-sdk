package com.sinch.verification.core.internal

/**
 * Interface collecting requirements each verification method must follow.
 * After constructing specific verification instance it has to be successfully initiated. Then depending on
 * specific method the verification code needs to be passed back to backend: either automatically intercepted
 * or manually typed by the user.
 */
interface Verification {

    /**
     * Current state of verification process.
     */
    val verificationState: VerificationState

    /**
     * Initiates the verification process.
     */
    fun initiate()

    /**
     * Verifies if provided code is correct.
     * @param verificationCode Code to be verified.
     * @param method Method of the verification if multiple submethods are available (auto verification).
     */
    fun verify(verificationCode: String, method: VerificationMethodType? = null)

    /**
     * Stops the verification process. You can still verify the code manually for given verification, however all
     * the automatic interceptors are stopped.
     */
    fun stop()
}