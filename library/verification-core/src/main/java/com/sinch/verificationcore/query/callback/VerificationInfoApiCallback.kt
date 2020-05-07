package com.sinch.verificationcore.query.callback

import com.sinch.verificationcore.internal.utils.ApiCallback
import com.sinch.verificationcore.verification.response.VerificationResponseData
import retrofit2.Response

/**
 * [ApiCallback] that passes retrofit call result to the final listener. (Simply hides [onSuccess] response parameter).
 */
class VerificationInfoApiCallback(private val verificationInfoCallback: VerificationInfoCallback) :
    ApiCallback<VerificationResponseData> {

    override fun onSuccess(
        data: VerificationResponseData,
        response: Response<VerificationResponseData>
    ) {
        verificationInfoCallback.onSuccess(data)
    }

    override fun onError(t: Throwable) {
        verificationInfoCallback.onError(t)
    }
}