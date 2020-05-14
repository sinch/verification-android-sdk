package com.sinch.verificationcore.internal

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
     */
    fun verify(verificationCode: String)

    /**
     * Stops the verification process. You can still verify the code manually for given verification, however all
     * the automatic interceptors are stopped.
     */
    fun stop()
}