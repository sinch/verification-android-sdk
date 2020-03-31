package com.sinch.verificationcore.internal.utils

import retrofit2.Response

interface ApiCallback<T> {
    fun onSuccess(data: T, response: Response<T>)
    fun onError(t: Throwable)
}