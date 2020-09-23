package com.sinch.verification.core.initiation

import com.sinch.logging.logger
import com.sinch.verification.core.initiation.response.InitiationListener
import com.sinch.verification.core.initiation.response.InitiationResponseData
import com.sinch.verification.core.internal.VerificationState
import com.sinch.verification.core.internal.VerificationStateListener
import com.sinch.verification.core.internal.VerificationStateStatus
import com.sinch.verification.core.internal.utils.ApiCallback
import retrofit2.Response

/**
 * General callback used by different verification methods to handle initiation API response.
 * @param T Type of initiation data returned by the api
 * @param listener Listener to be notified about initiation result.
 * @param dataModifier Function that is called before the data is passed to the [listener]. Can be used to modify it.
 * @param successCallback Callback invoked after successful api response with data returned by [dataModifier].
 */
open class InitiationApiCallback<T : InitiationResponseData>(
    private val listener: InitiationListener<T>,
    private val statusListener: VerificationStateListener,
    private val dataModifier: (T, Response<T>) -> T = { data, _ -> data },
    private val successCallback: (T) -> Unit = {}
) :
    ApiCallback<T> {

    private val logger = logger()

    override fun onSuccess(data: T, response: Response<T>) {
        ifNotManuallyStopped {
            val modifiedData = dataModifier(data, response)
            logger.debug("Verification initiated data is $data")
            statusListener.update(VerificationState.Initialization(VerificationStateStatus.SUCCESS))
            listener.onInitiated(modifiedData)
            successCallback(modifiedData)
        }
    }

    override fun onError(t: Throwable) {
        ifNotManuallyStopped {
            logger.debug("Verification initiation failed error is is $t")
            statusListener.update(VerificationState.Initialization(VerificationStateStatus.ERROR))
            listener.onInitializationFailed(t)
        }
    }

    private fun ifNotManuallyStopped(f: () -> Unit) {
        if (statusListener.verificationState != VerificationState.ManuallyStopped) {
            f()
        }
    }

}