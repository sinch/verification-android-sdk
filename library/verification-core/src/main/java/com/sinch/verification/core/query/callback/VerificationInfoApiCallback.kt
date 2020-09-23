package com.sinch.verification.core.query.callback

import com.sinch.verification.core.internal.utils.ApiCallback
import com.sinch.verification.core.verification.response.VerificationResponseData
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