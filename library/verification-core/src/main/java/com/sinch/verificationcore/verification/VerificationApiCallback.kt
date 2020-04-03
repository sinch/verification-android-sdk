package com.sinch.verificationcore.verification

import com.sinch.verificationcore.internal.VerificationState
import com.sinch.verificationcore.internal.VerificationStateListener
import com.sinch.verificationcore.internal.VerificationStateStatus
import com.sinch.verificationcore.internal.utils.ApiCallback
import com.sinch.verificationcore.verification.response.VerificationListener
import com.sinch.verificationcore.verification.response.VerificationResponseData
import retrofit2.Response

class VerificationApiCallback(
    private val listener: VerificationListener,
    private val verificationStateListener: VerificationStateListener
) :
    ApiCallback<VerificationResponseData> {

    override fun onSuccess(
        data: VerificationResponseData,
        response: Response<VerificationResponseData>
    ) {
        ifNotAlreadyVerified {
            verificationStateListener.update(VerificationState.Verification(VerificationStateStatus.SUCCESS))
            listener.onVerified()
        }
    }

    override fun onError(t: Throwable) {
        ifNotAlreadyVerified {
            verificationStateListener.update(VerificationState.Verification(VerificationStateStatus.ERROR))
            listener.onVerificationFailed(t)
        }
    }

    private fun ifNotAlreadyVerified(f: () -> Unit) {
        if (!verificationStateListener.verificationState.isVerified) {
            f()
        }
    }

}