package com.sinch.verification.callout

import com.sinch.verification.callout.initialization.CalloutInitializationResponseData
import com.sinch.verification.callout.initialization.CalloutVerificationInitializationData
import com.sinch.verification.callout.verification.CalloutVerificationData
import com.sinch.verificationcore.verification.response.VerificationResponseData
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * Retrofit service responsible for making API calls used by [CalloutVerificationMethod].
 */
interface CalloutVerificationService {

    /**
     * Initializes the verification process.
     * @param data Initiation data required by the Sinch API.
     * @return A [Call] object for the request.
     */
    @POST("verifications")
    fun initializeVerification(
        @Body data: CalloutVerificationInitializationData
    ): Call<CalloutInitializationResponseData>


    /**
     * Verifies if the verification code (text-to-speech in the incoming call) is correct.
     * @param number Number to be verified.
     * @param data Verification data required for callout verification API call.
     * @return A [Call] object for the request.
     */
    @PUT("verifications/number/{number}")
    fun verifyNumber(
        @Path("number") number: String,
        @Body data: CalloutVerificationData
    ): Call<VerificationResponseData>
}