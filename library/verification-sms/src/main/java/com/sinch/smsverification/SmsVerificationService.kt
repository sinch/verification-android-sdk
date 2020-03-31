package com.sinch.smsverification

import com.sinch.smsverification.initialization.SmsInitiationResponseData
import com.sinch.smsverification.initialization.SmsVerificationInitiationData
import com.sinch.smsverification.verification.SmsVerificationData
import com.sinch.verificationcore.verification.response.VerificationResponseData
import retrofit2.Call
import retrofit2.http.*

interface SmsVerificationService {

    @POST("verifications")
    fun initializeVerification(
        @Body data: SmsVerificationInitiationData,
        @Header("Accept-Language") acceptedLanguages: String?
    ): Call<SmsInitiationResponseData>

    @PUT("verifications/number/{number}")
    fun verifyNumber(
        @Path("number") number: String,
        @Body data: SmsVerificationData
    ): Call<VerificationResponseData>

}