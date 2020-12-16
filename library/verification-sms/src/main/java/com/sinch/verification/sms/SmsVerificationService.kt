package com.sinch.verification.sms

import com.sinch.verification.core.verification.VerificationService
import com.sinch.verification.sms.initialization.SmsInitiationResponseData
import com.sinch.verification.sms.initialization.SmsVerificationInitiationData
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * Retrofit service responsible for making API calls used by [SmsVerificationMethod].
 */
interface SmsVerificationService : VerificationService {

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

}