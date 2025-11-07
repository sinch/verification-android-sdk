package com.sinch.verification.sms

import com.sinch.logging.logger
import com.sinch.metadata.model.PhoneMetadataFactory
import com.sinch.verification.core.config.method.AutoInterceptedVerificationMethod
import com.sinch.verification.core.config.method.VerificationMethodCreator
import com.sinch.verification.core.initiation.InitiationApiCallback
import com.sinch.verification.core.initiation.VerificationIdentity
import com.sinch.verification.core.initiation.response.EmptyInitializationListener
import com.sinch.verification.core.internal.Verification
import com.sinch.verification.core.internal.VerificationMethodType
import com.sinch.verification.core.internal.utils.enqueue
import com.sinch.verification.core.verification.VerificationApiCallback
import com.sinch.verification.core.verification.asLanguagesString
import com.sinch.verification.core.verification.interceptor.CodeInterceptionListener
import com.sinch.verification.core.verification.model.VerificationSourceType
import com.sinch.verification.core.verification.model.sms.SmsVerificationDetails
import com.sinch.verification.core.verification.response.EmptyVerificationListener
import com.sinch.verification.core.verification.response.VerificationListener
import com.sinch.verification.sms.config.SmsVerificationConfig
import com.sinch.verification.sms.initialization.SmsInitializationListener
import com.sinch.verification.sms.initialization.SmsInitiationResponseData
import com.sinch.verification.sms.initialization.SmsOptions
import com.sinch.verification.sms.initialization.SmsVerificationInitiationData
import com.sinch.verification.sms.verification.SmsVerificationData
import com.sinch.verification.sms.verification.interceptor.SmsCodeInterceptor
import com.sinch.verification.utils.MAX_TIMEOUT

typealias EmptySmsInitializationListener = EmptyInitializationListener<SmsInitiationResponseData>
typealias SimpleInitializationSmsApiCallback = InitiationApiCallback<SmsInitiationResponseData>

/**
 * [Verification] that uses sms messages to verify user's phone number. The code that is received might be automatically
 * intercepted by [SmsCodeInterceptor] or manually typed by the user. Use [SmsVerificationMethod.Builder] to create an instance
 * of the verification.
 * @param config Reference to SMS configuration object.
 * @param initializationListener Listener to be notified about verification initiation result.
 * @param verificationListener Listener to be notified about the verification process result.
 */
class SmsVerificationMethod private constructor(
    private val config: SmsVerificationConfig,
    private val initializationListener: SmsInitializationListener = EmptySmsInitializationListener(),
    verificationListener: VerificationListener = EmptyVerificationListener()
) :
    AutoInterceptedVerificationMethod<SmsVerificationService, SmsCodeInterceptor>(
        config,
        verificationListener
    ),
    CodeInterceptionListener {

    private val metadataFactory: PhoneMetadataFactory = config.metadataFactory
    private val requestData: SmsVerificationInitiationData
        get() =
            SmsVerificationInitiationData(
                identity = VerificationIdentity(config.number.orEmpty()),
                honourEarlyReject = config.honourEarlyReject,
                custom = config.custom,
                reference = config.reference,
                metadata = metadataFactory.create(),
                smsOptions = SmsOptions(config.appHash)
            )

    override var codeInterceptor: SmsCodeInterceptor? =
        SmsCodeInterceptor(
            context = config.globalConfig.context,
            interceptionTimeout = Long.MAX_TIMEOUT,
            interceptionListener = this
        )

    override fun onInitiate() {
        logger.info("onInitiate called with requestData: $requestData")
        initializeInterceptorIfNeeded()
        verificationService.initializeVerification(
            requestData,
            config.acceptedLanguages.asLanguagesString()
        )
            .enqueue(
                retrofit = retrofit,
                apiCallback = SimpleInitializationSmsApiCallback(
                    listener = initializationListener,
                    statusListener = this,
                    dataModifier = { data, response -> data.copy(contentLanguage = response.headers()["Content-Language"].orEmpty()) },
                    successCallback = this::updateInterceptorWithApiData
                )
            )
    }

    override fun onVerify(
        verificationCode: String,
        sourceType: VerificationSourceType,
        method: VerificationMethodType?
    ) {
        val id = id ?: return
        verificationService.verifyById(
            verificationId = id,
            data = SmsVerificationData(sourceType, SmsVerificationDetails(verificationCode))
        ).enqueue(retrofit, VerificationApiCallback(verificationListener, this))
    }

    override fun onCodeIntercepted(code: String, source: VerificationSourceType) {
        verify(code, source)
    }

    override fun onCodeInterceptionError(e: Throwable) {
        verificationListener.onVerificationFailed(e)
    }

    private fun initializeInterceptorIfNeeded() {
        codeInterceptor?.start()
    }

    private fun updateInterceptorWithApiData(data: SmsInitiationResponseData) {
        logger.debug("Interceptor data update (API): $data")
        codeInterceptor?.apply {
            interceptionTimeout = data.details.interceptionTimeout
            smsTemplate = data.details.template
        }
    }

    /**
     * Builder implementing fluent builder pattern to create [SmsVerificationMethod] objects.
     */
    class Builder private constructor() :
        VerificationMethodCreator<SmsInitializationListener>, SmsVerificationConfigSetter {

        private val logger = logger()

        companion object {

            /**
             * Instance of builder that should be used to create [SmsVerificationMethod] object.
             */
            @JvmStatic
            val instance: SmsVerificationConfigSetter
                get() = Builder()

            operator fun invoke() = instance

        }

        private var initializationListener: SmsInitializationListener =
            EmptySmsInitializationListener()
        private var verificationListener: VerificationListener = EmptyVerificationListener()

        private lateinit var config: SmsVerificationConfig

        /**
         * Assigns config to the builder.
         * @param config Reference to SMS configuration object.
         * @return Instance of builder with assigned config.
         */
        override fun config(config: SmsVerificationConfig): VerificationMethodCreator<SmsInitializationListener> =
            apply {
                this.config = config
            }

        /**
         * Assigns verification listener to the builder.
         * @param verificationListener Listener to be notified about the verification process result.
         * @return Instance of builder with assigned verification listener.
         */
        override fun verificationListener(verificationListener: VerificationListener): VerificationMethodCreator<SmsInitializationListener> =
            apply {
                this.verificationListener = verificationListener
            }

        /**
         * Assigns initialization listener to the builder.
         * @param initializationListener Listener to be notified about verification initiation result.
         * @return Instance of builder with assigned initialization listener.
         */
        override fun initializationListener(initializationListener: SmsInitializationListener): VerificationMethodCreator<SmsInitializationListener> =
            apply {
                this.initializationListener = initializationListener
            }

        /**
         * Builds [SmsVerificationMethod] instance.
         * @return [Verification] instance with previously defined parameters.
         */
        override fun build(): Verification {
            return SmsVerificationMethod(
                config = config,
                initializationListener = initializationListener,
                verificationListener = verificationListener
            ).also {
                logger.debug("Created SmsVerificationMethod with config: $config")
            }
        }

    }


}