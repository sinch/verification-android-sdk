package com.sinch.verification.seamless

import com.sinch.verification.core.verification.VerificationService
import com.sinch.verification.seamless.initialization.SeamlessInitiationData
import com.sinch.verification.seamless.initialization.SeamlessInitiationResponseData
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Retrofit service responsible for making API calls used by [SeamlessVerificationMethod].
 */
interface SeamlessVerificationService : VerificationService {

    /**
     * Initializes the verification process.
     * @param data Initiation data required by the Sinch API.
     * @return A [Call] object for the request.
     */
    @POST("verifications")
    fun initializeVerification(@Body data: SeamlessInitiationData): Call<SeamlessInitiationResponseData>

}