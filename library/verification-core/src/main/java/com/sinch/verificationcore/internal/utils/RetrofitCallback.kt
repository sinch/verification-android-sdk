package com.sinch.verificationcore.internal.utils

import com.sinch.verificationcore.internal.error.ApiCallException
import com.sinch.verificationcore.internal.error.ApiErrorData
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

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
            response.body()?.let { apiCallback.onSuccess(it) }
        } else {
            response.errorBody()?.convertToError()
        }
    }

    private fun ResponseBody.convertToError() {
        val responseBodyConverter =
            retrofit.responseBodyConverter<ApiErrorData>(ApiErrorData::class.java, emptyArray())
        responseBodyConverter.convert(this)?.let { apiCallback.onError(ApiCallException(it)) }
    }

}