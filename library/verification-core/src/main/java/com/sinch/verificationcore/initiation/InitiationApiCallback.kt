package com.sinch.verificationcore.initiation

import com.sinch.verificationcore.initiation.response.InitiationListener
import com.sinch.verificationcore.initiation.response.InitiationResponseData
import com.sinch.verificationcore.internal.utils.ApiCallback

open class InitiationApiCallback<T : InitiationResponseData>(private val listener: InitiationListener<T>) :
    ApiCallback<T> {

    override fun onSuccess(data: T) =
        listener.onInitiated(data)

    override fun onError(t: Throwable) =
        listener.onInitializationFailed(t)

}