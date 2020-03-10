package com.sinch.verificationcore.network.service

import com.sinch.verificationcore.model.verification.VerificationRequestData
import com.sinch.verificationcore.model.verification.VerificationRequestResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface VerificationService {

    @POST("verifications")
    fun createVerificationRequest(@Body data: VerificationRequestData): Call<VerificationRequestResponse>

}