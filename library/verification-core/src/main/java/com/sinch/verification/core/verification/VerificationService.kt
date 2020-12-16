package com.sinch.verification.core.verification

import com.sinch.verification.core.verification.model.VerificationData
import com.sinch.verification.core.verification.response.VerificationResponseData
import retrofit2.Call
import retrofit2.http.*

interface VerificationService {

    /**
     * Verifies if given code is correct using the number.
     * @param number Number to be verified.
     * @param data Verification data required for sms verification API call.
     * @return A [Call] object for the request.
     */
    @PUT("verifications/number/{number}")
    fun verifyNumber(
        @Path("number") number: String,
        @Body data: VerificationData
    ): Call<VerificationResponseData>

    /**
     * Verifies if given code is correct.
     * @param subVerificationId ID assigned to specific method of the verification.
     * @param data Verification data required for auto verification API call.
     * @return A [Call] object for the request.
     */
    @PUT("verifications/id/{subVerificationId}")
    fun verifyById(
        @Path("subVerificationId") subVerificationId: String,
        @Body data: VerificationData
    ): Call<VerificationResponseData>

    /**
     * Verifies if the verification code (targetUri field) is correct.
     * @param url URI returned by seamless verification initialization call.
     * @return A [Call] object for the request.
     */
    @GET
    fun verifySeamless(@Url url: String): Call<VerificationResponseData>

}