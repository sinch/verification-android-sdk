package com.sinch.verification.all.auto

import com.sinch.verification.all.auto.initialization.AutoInitializationData
import com.sinch.verification.all.auto.initialization.AutoInitializationResponseData
import com.sinch.verification.core.verification.VerificationService
import com.sinch.verification.core.verification.response.VerificationResponseData
import com.sinch.verification.seamless.SeamlessCallbackBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT

interface AutoVerificationService : VerificationService {

    /**
     * Initializes the verification process.
     * @param data Initiation data required by the Sinch API.
     * @return A [Call] object for the request.
     */
    @POST("verifications")
    fun initializeVerification(@Body data: AutoInitializationData): Call<AutoInitializationResponseData>

    @PUT("verifications/seamless-callback/aduna-v2")
    fun verifySeamlessWithCredential(
        @Body body: SeamlessCallbackBody
    ): Call<VerificationResponseData>
}
