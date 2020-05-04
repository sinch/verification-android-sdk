package com.sinch.verificationcore.initiation

import com.sinch.verificationcore.initiation.response.InitiationListener
import com.sinch.verificationcore.initiation.response.InitiationResponseData
import com.sinch.verificationcore.internal.VerificationState
import com.sinch.verificationcore.internal.VerificationStateListener
import com.sinch.verificationcore.internal.VerificationStateStatus
import com.sinch.verificationcore.internal.utils.ApiCallback
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

    override fun onSuccess(data: T, response: Response<T>) {
        val modifiedData = dataModifier(data, response)
        statusListener.update(VerificationState.Initialization(VerificationStateStatus.SUCCESS))
        listener.onInitiated(modifiedData)
        successCallback(modifiedData)
    }

    override fun onError(t: Throwable) {
        statusListener.update(VerificationState.Initialization(VerificationStateStatus.ERROR))
        listener.onInitializationFailed(t)
    }

}