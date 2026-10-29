package com.sinch.verification.seamless

import com.sinch.verification.core.verification.VerificationService
import com.sinch.verification.core.verification.response.VerificationResponseData
import com.sinch.verification.seamless.initialization.SeamlessInitiationData
import com.sinch.verification.seamless.initialization.SeamlessInitiationResponseData
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT

@Serializable
data class SeamlessCallbackBody(
    @SerialName("state") val state: String,
    @SerialName("OperatorToken") val operatorToken: String
)

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

    /**
     * Verifies the seamless process using a digital credential presentation (v2 flow).
     * @param body Request body containing the verification state and operator token.
     * @return A [Call] object for the request.
     */
    @PUT("verifications/seamless-callback/aduna-v2")
    fun verifySeamlessWithCredential(
        @Body body: SeamlessCallbackBody
    ): Call<VerificationResponseData>

}
