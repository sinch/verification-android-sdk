package com.sinch.verification.flashcall

import com.sinch.verification.flashcall.initialization.FlashCallInitializationResponseData
import com.sinch.verification.flashcall.initialization.FlashCallVerificationInitializationData
import com.sinch.verification.flashcall.report.FlashCallReportData
import com.sinch.verification.flashcall.verification.FlashCallVerificationData
import com.sinch.verificationcore.verification.response.VerificationResponseData
import retrofit2.Call
import retrofit2.http.*

interface FlashCallVerificationService {

    @POST("verifications")
    fun initializeVerification(
        @Body data: FlashCallVerificationInitializationData
    ): Call<FlashCallInitializationResponseData>

    @PUT("verifications/number/{number}")
    fun verifyNumber(
        @Path("number") number: String,
        @Body data: FlashCallVerificationData
    ): Call<VerificationResponseData>

    @PATCH("verifications/number/{number}")
    fun reportVerification(
        @Path("number") number: String,
        @Body data: FlashCallReportData
    ): Call<Unit>

}