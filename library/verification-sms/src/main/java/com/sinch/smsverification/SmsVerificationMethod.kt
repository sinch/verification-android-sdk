package com.sinch.smsverification

import com.sinch.verificationcore.config.method.VerificationMethod
import com.sinch.verificationcore.request.VerificationIdentity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SmsVerificationMethod(config: SmsVerificationConfig) :
    VerificationMethod<SmsVerificationService>(config) {

    private val number: String = config.number
    private val custom: String = config.custom

    private val requestDataData: SmsVerificationRequestData
        get() =
            SmsVerificationRequestData(VerificationIdentity(number), true, null)

    override fun initiate() {
        verificationService.initializeVerification(requestDataData).enqueue(object :
            Callback<SmsVerificationResponse> {

            override fun onFailure(call: Call<SmsVerificationResponse>, t: Throwable) {}

            override fun onResponse(
                call: Call<SmsVerificationResponse>,
                response: Response<SmsVerificationResponse>
            ) {
            }

        })
    }

    override fun verify(verificationCode: String) {
        TODO("Not yet implemented")
    }


}