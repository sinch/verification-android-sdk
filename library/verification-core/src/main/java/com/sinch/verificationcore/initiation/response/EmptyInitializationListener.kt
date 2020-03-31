package com.sinch.verificationcore.initiation.response

class EmptyInitializationListener<T : InitiationResponseData> : InitiationListener<T> {
    override fun onInitiated(data: T, contentLanguage: String) {}
    override fun onInitializationFailed(t: Throwable) {}
}