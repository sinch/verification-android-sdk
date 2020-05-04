package com.sinch.verificationcore.initiation.response

/**
 * Convenient [InitiationListener] with empty method implementations.
 * @param T Type of initiation response data.
 */
class EmptyInitializationListener<T : InitiationResponseData> : InitiationListener<T> {
    override fun onInitiated(data: T) {}
    override fun onInitializationFailed(t: Throwable) {}
}