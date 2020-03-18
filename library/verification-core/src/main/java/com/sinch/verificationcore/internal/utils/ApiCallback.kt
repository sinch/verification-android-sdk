package com.sinch.verificationcore.internal.utils

interface ApiCallback<T> {
    fun onSuccess(data: T)
    fun onError(t: Throwable)
}