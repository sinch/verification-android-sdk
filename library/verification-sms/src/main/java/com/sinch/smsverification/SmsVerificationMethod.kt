package com.sinch.smsverification

import com.sinch.metadata.model.PhoneMetadataFactory
import com.sinch.smsverification.config.SmsVerificationConfig
import com.sinch.smsverification.initialization.SmsInitializationListener
import com.sinch.smsverification.initialization.SmsInitiationResponseData
import com.sinch.smsverification.initialization.SmsVerificationInitiationData
import com.sinch.verificationcore.config.method.VerificationMethod
import com.sinch.verificationcore.initiation.SimpleInitiationApiCallback
import com.sinch.verificationcore.initiation.VerificationIdentity
import com.sinch.verificationcore.initiation.response.EmptyInitializationListener
import com.sinch.verificationcore.internal.utils.enqueue

typealias  EmptySmsInitializationListener = EmptyInitializationListener<SmsInitiationResponseData>
typealias  SimpleInitializationSmsApiCallback = SimpleInitiationApiCallback<SmsInitiationResponseData>

class SmsVerificationMethod(
    private val config: SmsVerificationConfig,
    val initializationListener: SmsInitializationListener = EmptySmsInitializationListener()
) :
    VerificationMethod<SmsVerificationService>(config) {

    private val retrofit get() = generalConfig.retrofit
    private val metadataFactory: PhoneMetadataFactory = config.metadataFactory

    private val requestDataData: SmsVerificationInitiationData
        get() =
            SmsVerificationInitiationData(
                VerificationIdentity(config.number),
                config.honourEarlyReject,
                config.custom,
                metadataFactory.create()
            )

    override fun initiate() {
        verificationService.initializeVerification(requestDataData)
            .enqueue(retrofit, SimpleInitializationSmsApiCallback(initializationListener))
    }

    override fun verify(verificationCode: String) {
        TODO("Not yet implemented")
    }

}