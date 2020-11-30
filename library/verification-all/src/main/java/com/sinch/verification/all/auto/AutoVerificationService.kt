package com.sinch.verification.all.auto

import com.sinch.verification.all.auto.initialization.AutoInitializationData
import com.sinch.verification.all.auto.initialization.AutoInitializationResponseData
import com.sinch.verification.all.auto.verification.AutoVerificationData
import com.sinch.verification.core.verification.response.VerificationResponseData
import com.sinch.verification.seamless.initialization.SeamlessInitializationDetails
import retrofit2.Call
import retrofit2.http.*

interface AutoVerificationService {

    /**
     * Initializes the verification process.
     * @param data Initiation data required by the Sinch API.
     * @return A [Call] object for the request.
     */
    @POST("verifications")
    fun initializeVerification(@Body data: AutoInitializationData): Call<AutoInitializationResponseData>

    /**
     * Verifies if given code is correct.
     * @param subVerificationId ID assigned to specific method of the verification.
     * @param data Verification data required for auto verification API call.
     * @return A [Call] object for the request.
     */
    @PUT("verifications/id/{subVerificationId}")
    fun verifyNumber(
        @Path("subVerificationId") subVerificationId: String,
        @Body data: AutoVerificationData
    ): Call<VerificationResponseData>

    /**
     * Verifies if the verification code (targetUri field) is correct.
     * @param url URI returned with [initializeVerification] call.
     * @return A [Call] object for the request.
     * @see SeamlessInitializationDetails.targetUri
     */
    @GET
    fun verifySeamless(@Url url: String): Call<VerificationResponseData>
}