package com.sinch.smsverification

import com.sinch.smsverification.config.SmsVerificationConfig
import com.sinch.smsverification.initialization.SmsInitializationListener
import com.sinch.smsverification.initialization.SmsInitiationResponseData
import com.sinch.smsverification.initialization.SmsVerificationInitiationData
import com.sinch.verificationcore.config.method.VerificationMethod
import com.sinch.verificationcore.initiation.VerificationIdentity
import com.sinch.verificationcore.initiation.response.EmptyInitializationListener
import com.sinch.verificationcore.internal.utils.ApiCallback
import com.sinch.verificationcore.internal.utils.enqueue

typealias  EmptySmsInitializationListener = EmptyInitializationListener<SmsInitiationResponseData>

class SmsVerificationMethod(
    config: SmsVerificationConfig,
    val initializationListener: SmsInitializationListener = EmptySmsInitializationListener()
) :
    VerificationMethod<SmsVerificationService>(config) {

    private val retrofit get() = generalConfig.retrofit

    private val number: String = config.number
    private val custom: String = config.custom

    private val requestDataData: SmsVerificationInitiationData
        get() =
            SmsVerificationInitiationData(
                VerificationIdentity(number),
                true,
                custom
            )

    override fun initiate() {
        verificationService.initializeVerification(requestDataData)
            .enqueue(retrofit, object : ApiCallback<SmsInitiationResponseData> {
                override fun onSuccess(data: SmsInitiationResponseData) {
                    initializationListener.onInitiated(data)
                }

                override fun onError(t: Throwable) {
                    initializationListener.onInitializationFailed(t)
                }
            })
    }

    override fun verify(verificationCode: String) {
        TODO("Not yet implemented")
    }


}