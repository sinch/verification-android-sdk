package com.sinch.verification.seamless

import com.sinch.verification.seamless.initialization.SeamlessInitializationDetails
import com.sinch.verification.seamless.initialization.SeamlessInitiationData
import com.sinch.verification.seamless.initialization.SeamlessInitiationResponseData
import com.sinch.verification.core.verification.response.VerificationResponseData
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url

/**
 * Retrofit service responsible for making API calls used by [SeamlessVerificationMethod].
 */
interface SeamlessVerificationService {

    /**
     * Initializes the verification process.
     * @param data Initiation data required by the Sinch API.
     * @return A [Call] object for the request.
     */
    @POST("verifications")
    fun initializeVerification(@Body data: SeamlessInitiationData): Call<SeamlessInitiationResponseData>

    /**
     * Verifies if the verification code (targetUri field) is correct.
     * @param url URI returned with [initializeVerification] call.
     * @return A [Call] object for the request.
     * @see SeamlessInitializationDetails.targetUri
     */
    @GET
    fun verifySeamless(@Url url: String): Call<VerificationResponseData>

}