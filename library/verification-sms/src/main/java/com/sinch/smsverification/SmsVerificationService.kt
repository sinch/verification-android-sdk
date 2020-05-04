package com.sinch.smsverification

import com.sinch.smsverification.initialization.SmsInitiationResponseData
import com.sinch.smsverification.initialization.SmsVerificationInitiationData
import com.sinch.smsverification.verification.SmsVerificationData
import com.sinch.verificationcore.verification.response.VerificationResponseData
import retrofit2.Call
import retrofit2.http.*

/**
 * Retrofit service responsible for making API calls used by [SmsVerificationMethod].
 */
interface SmsVerificationService {

    /**
     * Initializes the verification process.
     * @param data Initiation data required by the Sinch API.
     * @param acceptedLanguages List of languages the sms message with the verification code will be written in.
     * @return A [Call] object for the request.
     */
    @POST("verifications")
    fun initializeVerification(
        @Body data: SmsVerificationInitiationData,
        @Header("Accept-Language") acceptedLanguages: String?
    ): Call<SmsInitiationResponseData>

    /**
     * Verifies if given code is correct.
     * @param number Number to be verified.
     * @param data Verification data required for sms verification API call.
     * @return A [Call] object for the request.
     */
    @PUT("verifications/number/{number}")
    fun verifyNumber(
        @Path("number") number: String,
        @Body data: SmsVerificationData
    ): Call<VerificationResponseData>

}