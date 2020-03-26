package com.sinch.verificationcore.verification

import com.sinch.verificationcore.internal.utils.ApiCallback
import com.sinch.verificationcore.verification.response.VerificationListener
import com.sinch.verificationcore.verification.response.VerificationResponseData

class VerificationApiCallback(private val listener: VerificationListener) :
    ApiCallback<VerificationResponseData> {

    override fun onSuccess(data: VerificationResponseData) =
        listener.onVerified()

    override fun onError(t: Throwable) =
        listener.onVerificationFailed(t)
}