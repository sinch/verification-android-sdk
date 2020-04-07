package com.sinch.smsverification

import com.sinch.metadata.model.PhoneMetadataFactory
import com.sinch.smsverification.config.SmsVerificationConfig
import com.sinch.smsverification.initialization.SmsInitializationListener
import com.sinch.smsverification.initialization.SmsInitiationResponseData
import com.sinch.smsverification.initialization.SmsOptions
import com.sinch.smsverification.initialization.SmsVerificationInitiationData
import com.sinch.smsverification.verification.SmsVerificationData
import com.sinch.smsverification.verification.SmsVerificationDetails
import com.sinch.smsverification.verification.extractor.SmsCodeExtractor
import com.sinch.smsverification.verification.interceptor.SmsCodeInterceptor
import com.sinch.verificationcore.config.method.VerificationMethod
import com.sinch.verificationcore.config.method.VerificationMethodCreator
import com.sinch.verificationcore.initiation.InitiationApiCallback
import com.sinch.verificationcore.initiation.VerificationIdentity
import com.sinch.verificationcore.initiation.response.EmptyInitializationListener
import com.sinch.verificationcore.internal.Verification
import com.sinch.verificationcore.internal.utils.enqueue
import com.sinch.verificationcore.verification.VerificationApiCallback
import com.sinch.verificationcore.verification.VerificationSourceType
import com.sinch.verificationcore.verification.interceptor.CodeInterceptionListener
import com.sinch.verificationcore.verification.response.EmptyVerificationListener
import com.sinch.verificationcore.verification.response.VerificationListener
import retrofit2.Response

typealias  EmptySmsInitializationListener = EmptyInitializationListener<SmsInitiationResponseData>
typealias  SimpleInitializationSmsApiCallback = InitiationApiCallback<SmsInitiationResponseData>

class SmsVerificationMethod private constructor(
    private val config: SmsVerificationConfig,
    private val initializationListener: SmsInitializationListener = EmptySmsInitializationListener(),
    verificationListener: VerificationListener = EmptyVerificationListener()
) :
    VerificationMethod<SmsVerificationService>(config, verificationListener),
    CodeInterceptionListener {

    private val retrofit get() = globalConfig.retrofit
    private val metadataFactory: PhoneMetadataFactory = config.metadataFactory
    private val requestDataData: SmsVerificationInitiationData
        get() =
            SmsVerificationInitiationData(
                identity = VerificationIdentity(config.number),
                honourEarlyReject = config.honourEarlyReject,
                custom = config.custom,
                metadata = metadataFactory.create(),
                smsOptions = SmsOptions(config.appHash)
            )

    private var smsCodeInterceptor: SmsCodeInterceptor? = null

    override fun onInitiate() {
        verificationService.initializeVerification(
            requestDataData,
            config.acceptedLanguages.asLanguagesString()
        )
            .enqueue(
                retrofit,
                object : SimpleInitializationSmsApiCallback(initializationListener, this) {
                    override fun onSuccess(
                        data: SmsInitiationResponseData,
                        response: Response<SmsInitiationResponseData>
                    ) {
                        super.onSuccess(data, response)
                        initializeInterceptorIfNeeded(data.details.template)
                    }
                })
    }

    override fun onVerify(verificationCode: String, sourceType: VerificationSourceType) {
        verificationService.verifyNumber(
            number = config.number,
            data = SmsVerificationData(sourceType, SmsVerificationDetails(verificationCode))
        ).enqueue(retrofit, VerificationApiCallback(verificationListener, this))
    }

    override fun onCodeIntercepted(code: String) {
        verify(code, VerificationSourceType.INTERCEPTION)
    }

    override fun onCodeInterceptionError(e: Throwable) {
        verificationListener.onVerificationFailed(e)
    }

    private fun initializeInterceptorIfNeeded(template: String) {
        if (config.appHash.isNullOrBlank()) {
            logger.info("App hash not provided, skipping initialization of interceptor")
        } else {
            initializeInterceptor(template)
        }
    }

    private fun initializeInterceptor(template: String) {
        try {
            val templateMatcher = SmsCodeExtractor(template)
            smsCodeInterceptor = SmsCodeInterceptor(
                context = config.globalConfig.context,
                smsCodeExtractor = templateMatcher,
                maxTimeout = config.maxTimeout,
                interceptionListener = this
            )
            smsCodeInterceptor?.start()
        } catch (e: Exception) {
            verificationListener.onVerificationFailed(e)
        }
    }

    private fun List<String>.asLanguagesString() =
        if (isEmpty()) null else reduce { acc, s -> "$acc,$s" }

    class Builder private constructor() :
        VerificationMethodCreator<SmsInitializationListener>, SmsVerificationConfigSetter {

        companion object {
            @JvmStatic
            val instance: SmsVerificationConfigSetter
                get() = Builder()
        }

        private var initializationListener: SmsInitializationListener =
            EmptySmsInitializationListener()
        private var verificationListener: VerificationListener = EmptyVerificationListener()

        private lateinit var config: SmsVerificationConfig

        override fun config(config: SmsVerificationConfig): VerificationMethodCreator<SmsInitializationListener> =
            apply {
                this.config = config
            }

        override fun verificationListener(verificationListener: VerificationListener): VerificationMethodCreator<SmsInitializationListener> =
            apply {
                this.verificationListener = verificationListener
            }

        override fun initializationListener(initializationListener: SmsInitializationListener): VerificationMethodCreator<SmsInitializationListener> =
            apply {
                this.initializationListener = initializationListener
            }

        override fun build(): Verification {
            return SmsVerificationMethod(
                config = config,
                initializationListener = initializationListener,
                verificationListener = verificationListener
            )
        }

    }

}