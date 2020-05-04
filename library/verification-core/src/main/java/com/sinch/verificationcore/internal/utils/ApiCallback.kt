package com.sinch.verificationcore.internal.utils

import retrofit2.Response

/**
 * Interface used for implementing API callbacks from Retrofit services.
 * @param T Type of successful response data content.
 */
interface ApiCallback<T> {

    /**
     * Called after the call was successful.
     * @see RetrofitCallback
     * @param data Data of the API call.
     * @param response Field containing detailed information about the response.
     */
    fun onSuccess(data: T, response: Response<T>)

    /**
     * Called after the call has failed.
     * @param t Error describing the reason why the call has failed.
     */
    fun onError(t: Throwable)
}