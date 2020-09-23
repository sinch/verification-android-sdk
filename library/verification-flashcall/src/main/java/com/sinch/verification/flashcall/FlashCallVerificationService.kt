package com.sinch.verification.flashcall

import com.sinch.verification.flashcall.initialization.FlashCallInitializationResponseData
import com.sinch.verification.flashcall.initialization.FlashCallVerificationInitializationData
import com.sinch.verification.flashcall.report.FlashCallReportData
import com.sinch.verification.flashcall.verification.FlashCallVerificationData
import com.sinch.verification.core.verification.response.VerificationResponseData
import retrofit2.Call
import retrofit2.http.*

/**
 * Retrofit service responsible for making API calls used by [FlashCallVerificationMethod].
 */
interface FlashCallVerificationService {

    /**
     * Initializes the verification process.
     * @param data Initiation data required by the Sinch API.
     * @return A [Call] object for the request.
     */
    @POST("verifications")
    fun initializeVerification(
        @Body data: FlashCallVerificationInitializationData
    ): Call<FlashCallInitializationResponseData>

    /**
     * Verifies if the verification code (incoming flashcall number) is correct.
     * @param number Number to be verified.
     * @param data Verification data required for flashcall verification API call.
     * @return A [Call] object for the request.
     */
    @PUT("verifications/number/{number}")
    fun verifyNumber(
        @Path("number") number: String,
        @Body data: FlashCallVerificationData
    ): Call<VerificationResponseData>

    /**
     * Sends verification analytics data to the backend.
     * @param number Phone number that was being verified.
     * @param data Analytics data containing information about incoming flashcall.
     */
    @PATCH("verifications/number/{number}")
    fun reportVerification(
        @Path("number") number: String,
        @Body data: FlashCallReportData
    ): Call<Unit>

}