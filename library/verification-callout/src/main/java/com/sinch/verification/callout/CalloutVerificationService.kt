package com.sinch.verification.callout

import com.sinch.verification.callout.initialization.CalloutInitializationResponseData
import com.sinch.verification.callout.initialization.CalloutVerificationInitializationData
import com.sinch.verification.core.verification.VerificationService
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Retrofit service responsible for making API calls used by [CalloutVerificationMethod].
 */
interface CalloutVerificationService : VerificationService {

    /**
     * Initializes the verification process.
     * @param data Initiation data required by the Sinch API.
     * @return A [Call] object for the request.
     */
    @POST("verifications")
    fun initializeVerification(
        @Body data: CalloutVerificationInitializationData
    ): Call<CalloutInitializationResponseData>

}