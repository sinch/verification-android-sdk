package com.sinch.verificationcore.verification

import com.sinch.logging.logger
import com.sinch.verificationcore.internal.VerificationState
import com.sinch.verificationcore.internal.VerificationStateListener
import com.sinch.verificationcore.internal.VerificationStateStatus
import com.sinch.verificationcore.internal.VerificationStatus
import com.sinch.verificationcore.internal.error.VerificationException
import com.sinch.verificationcore.internal.utils.ApiCallback
import com.sinch.verificationcore.verification.response.VerificationListener
import com.sinch.verificationcore.verification.response.VerificationResponseData
import retrofit2.Response

/**
 * [ApiCallback] used by different verification methods that handles verification API call result,
 * makes sure the process has finished successfully and notifies the [VerificationListener].
 */
class VerificationApiCallback(
    private val listener: VerificationListener,
    private val verificationStateListener: VerificationStateListener,
    private val beforeResultHandledCallback: () -> Unit = { }
) :
    ApiCallback<VerificationResponseData> {

    private val logger = logger()

    override fun onSuccess(
        data: VerificationResponseData,
        response: Response<VerificationResponseData>
    ) {
        beforeResultHandledCallback()
        ifNotAlreadyVerified {
            /*
            In some case even though we got 200 status code the status field is set to ERROR.
             */
            if (data.status == VerificationStatus.SUCCESSFUL) {
                handleSuccessfulVerification()
            } else {
                handleError(VerificationException(data.errorReason.orEmpty()))
            }
        }
    }

    override fun onError(t: Throwable) {
        beforeResultHandledCallback()
        ifNotAlreadyVerified {
            handleError(t)
        }
    }

    private fun handleSuccessfulVerification() {
        logger.debug("Verification call successful!")
        verificationStateListener.update(
            VerificationState.Verification(
                VerificationStateStatus.SUCCESS
            )
        )
        listener.onVerified()
    }

    private fun handleError(t: Throwable) {
        logger.debug("Verification call failed with error $t")
        verificationStateListener.update(VerificationState.Verification(VerificationStateStatus.ERROR))
        listener.onVerificationFailed(t)
    }

    /**
     * Makes sure the user has not already been verified by the verification instance.
     * @param f callback invoked if the verification process has not finished.
     */
    private fun ifNotAlreadyVerified(f: () -> Unit) {
        if (!verificationStateListener.verificationState.isVerified) {
            f()
        }
    }

}