package com.sinch.verification.core.query

import com.sinch.verification.core.internal.VerificationMethodType
import com.sinch.verification.core.verification.response.VerificationResponseData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Retrofit service used by [SinchVerificationQuery] used for querying for verifications.
 */
interface VerificationQueryService {

    /**
     * Queries for verification information based on verification id.
     * @param id Id of the verification to be queried.
     * @return A [Call] object for the request.
     */
    @GET("verifications/id/{id}")
    fun queryById(@Path("id") id: String): Call<VerificationResponseData>

    /**
     * Queries for verification information based on verification reference.
     * @param reference Reference of the verification to be queried.
     * @return A [Call] object for the request.
     */
    @GET("verifications/reference/{reference}")
    fun queryByReference(@Path("reference") reference: String): Call<VerificationResponseData>

    /**
     * Queries for verification based on number and method used for the verification.
     * NOTE: This query is valid only for a limited time after the verification process has started.
     * @param method Method used for the verification.
     * @param number
     * @return A [Call] object for the request.
     */
    @GET("verifications/{method}/number/{number}")
    fun queryByEndpoint(
        @Path("method") method: VerificationMethodType,
        @Path("number") number: String
    ): Call<VerificationResponseData>

}