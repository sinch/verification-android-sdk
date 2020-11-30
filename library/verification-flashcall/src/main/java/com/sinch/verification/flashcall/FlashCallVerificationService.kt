package com.sinch.verification.flashcall

import com.sinch.verification.core.verification.VerificationService
import com.sinch.verification.flashcall.initialization.FlashCallInitializationResponseData
import com.sinch.verification.flashcall.initialization.FlashCallVerificationInitializationData
import com.sinch.verification.flashcall.report.FlashCallReportData
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Retrofit service responsible for making API calls used by [FlashCallVerificationMethod].
 */
interface FlashCallVerificationService : VerificationService {

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