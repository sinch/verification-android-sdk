package com.sinch.verification.core.internal.utils

import retrofit2.Call
import retrofit2.Retrofit

/**
 * Convenient call that uses [RetrofitCallback] to handle Sinch API error codes and error bodies before passing the result to actual [ApiCallback].
 * @param retrofit Retrofit instance used for making API calls.
 * @param apiCallback Callback invoked after the call was made and processed.
 */
fun <T> Call<T>.enqueue(retrofit: Retrofit, apiCallback: ApiCallback<T>) {
    enqueue(RetrofitCallback(retrofit, apiCallback))
}