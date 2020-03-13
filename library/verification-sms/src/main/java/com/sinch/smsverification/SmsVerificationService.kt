package com.sinch.smsverification

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface SmsVerificationService {

    @POST("verifications")
    fun initializeVerification(@Body data: SmsVerificationRequestData): Call<SmsVerificationResponse>

}