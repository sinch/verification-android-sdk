package com.sinch.verificationcore.internal.utils

import retrofit2.Call
import retrofit2.Retrofit

fun <T> Call<T>.enqueue(retrofit: Retrofit, apiCallback: ApiCallback<T>) {
    enqueue(RetrofitCallback(retrofit, apiCallback))
}