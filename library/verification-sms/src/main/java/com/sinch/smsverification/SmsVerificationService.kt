package com.sinch.smsverification

import com.sinch.smsverification.initialization.SmsInitiationResponseData
import com.sinch.smsverification.initialization.SmsVerificationInitiationData
import com.sinch.smsverification.verification.SmsVerificationData
import com.sinch.verificationcore.verification.response.VerificationResponseData
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface SmsVerificationService {

    @POST("verifications")
    fun initializeVerification(@Body data: SmsVerificationInitiationData): Call<SmsInitiationResponseData>

    @PUT("verifications/number/{number}")
    fun verifyNumber(
        @Path("number") number: String,
        @Body data: SmsVerificationData
    ): Call<VerificationResponseData>

}