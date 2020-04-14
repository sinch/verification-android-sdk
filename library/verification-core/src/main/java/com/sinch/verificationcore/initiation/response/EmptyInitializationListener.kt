package com.sinch.verificationcore.initiation.response

class EmptyInitializationListener<T : InitiationResponseData> : InitiationListener<T> {
    override fun onInitiated(data: T) {}
    override fun onInitializationFailed(t: Throwable) {}
}