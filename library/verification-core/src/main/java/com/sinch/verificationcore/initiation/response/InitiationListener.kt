package com.sinch.verificationcore.initiation.response

interface InitiationListener<T : InitiationResponseData> {
    fun onInitiated(data: T)
    fun onInitializationFailed(t: Throwable)
}