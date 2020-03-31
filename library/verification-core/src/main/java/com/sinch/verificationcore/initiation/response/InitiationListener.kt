package com.sinch.verificationcore.initiation.response

interface InitiationListener<T : InitiationResponseData> {
    fun onInitiated(data: T, contentLanguage: String)
    fun onInitializationFailed(t: Throwable)
}