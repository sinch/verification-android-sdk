package com.sinch.verificationcore.initiation.response

/**
 * Interface defining methods notifying about verification initiation process result.
 * @param T Type of successful initiation result data.
 */
interface InitiationListener<in T : InitiationResponseData> {

    /**
     * Called if the initiation process has finished successfully.
     * @param data Extra data that might be required during actual verification process.
     */
    fun onInitiated(data: T)

    /**
     * Called when the initiation process has failed.
     * @param t Error data.
     */
    fun onInitializationFailed(t: Throwable)
}