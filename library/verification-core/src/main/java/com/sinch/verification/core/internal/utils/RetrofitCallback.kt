package com.sinch.verification.core.internal.utils

import com.sinch.verification.core.internal.error.ApiCallException
import com.sinch.verification.core.internal.error.ApiErrorData
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

/**
 * Retrofit callback wrapper that checks if the server returned success code and
 * handles API errors conversion before passing it to the listener (apiCallback).
 * @param retrofit Retrofit instance used for making API calls.
 * @param apiCallback Callback invoked after the call was made and processed.
 */
open class RetrofitCallback<T>(
    private val retrofit: Retrofit,
    private val apiCallback: ApiCallback<T>
) :
    Callback<T> {

    override fun onFailure(call: Call<T>, t: Throwable) {
        apiCallback.onError(t)
    }

    override fun onResponse(call: Call<T>, response: Response<T>) {
        if (response.isSuccessful) {
            response.body()?.let { apiCallback.onSuccess(it, response) }
        } else {
            response.errorBody()?.convertToError()
        }
    }

    private fun ResponseBody.convertToError() {
        val responseBodyConverter =
            retrofit.responseBodyConverter<ApiErrorData>(ApiErrorData::class.java, emptyArray())
        try {
            responseBodyConverter.convert(this)?.let { apiCallback.onError(ApiCallException(it)) }
        } catch (e: Exception) {
            apiCallback.onError(e)
        }
    }

}