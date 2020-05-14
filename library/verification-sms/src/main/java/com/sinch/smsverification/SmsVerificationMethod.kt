package com.sinch.smsverification

import com.sinch.metadata.model.PhoneMetadataFactory
import com.sinch.smsverification.config.SmsVerificationConfig
import com.sinch.smsverification.initialization.SmsInitializationListener
import com.sinch.smsverification.initialization.SmsInitiationResponseData
import com.sinch.smsverification.initialization.SmsOptions
import com.sinch.smsverification.initialization.SmsVerificationInitiationData
import com.sinch.smsverification.verification.SmsVerificationData
import com.sinch.smsverification.verification.SmsVerificationDetails
import com.sinch.smsverification.verification.interceptor.SmsCodeInterceptor
import com.sinch.utils.MAX_TIMEOUT
import com.sinch.verificationcore.config.method.AutoInterceptedVerificationMethod
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

typealias  EmptySmsInitializationListener = EmptyInitializationListener<SmsInitiationResponseData>
typealias  SimpleInitializationSmsApiCallback = InitiationApiCallback<SmsInitiationResponseData>

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
    AutoInterceptedVerificationMethod<SmsVerificationService, SmsCodeInterceptor>(config, verificationListener),
    CodeInterceptionListener {

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

    override var codeInterceptor: SmsCodeInterceptor? =
        SmsCodeInterceptor(
            context = config.globalConfig.context,
            maxTimeout = config.maxTimeout ?: Long.MAX_TIMEOUT,
            interceptionListener = this
        )

    override fun onInitiate() {
        initializeInterceptorIfNeeded()
        verificationService.initializeVerification(
            requestDataData,
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

    override fun onVerify(verificationCode: String, sourceType: VerificationSourceType) {
        verificationService.verifyNumber(
            number = config.number,
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
        if (config.appHash.isNullOrBlank()) {
            logger.info("App hash not provided, skipping initialization of interceptor")
        } else {
            codeInterceptor?.start()
        }
    }

    private fun updateInterceptorWithApiData(data: SmsInitiationResponseData) {
        codeInterceptor?.apply {
            maxTimeout = chooseMaxTimeout(
                userDefined = config.maxTimeout,
                apiResponseTimeout = data.details.interceptionTimeout
            )
            smsTemplate = data.details.template
        }
    }

    private fun List<String>.asLanguagesString() =
        if (isEmpty()) null else reduce { acc, s -> "$acc,$s" }

    /**
     * Builder implementing fluent builder pattern to create [SmsVerificationMethod] objects.
     */
    class Builder private constructor() :
        VerificationMethodCreator<SmsInitializationListener>, SmsVerificationConfigSetter {

        companion object {

            /**
             * Instance of builder that should be used to create [SmsVerificationMethod] object.
             */
            @JvmStatic
            val instance: SmsVerificationConfigSetter
                get() = Builder()
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
            )
        }

    }


}