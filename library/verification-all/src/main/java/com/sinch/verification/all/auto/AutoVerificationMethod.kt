package com.sinch.verification.all.auto

import com.sinch.logging.logger
import com.sinch.verification.all.auto.config.AutoVerificationConfig
import com.sinch.verification.all.auto.initialization.AutoInitializationData
import com.sinch.verification.all.auto.initialization.AutoInitializationListener
import com.sinch.verification.all.auto.initialization.AutoInitializationResponseData
import com.sinch.verification.all.auto.verification.AutoVerificationData
import com.sinch.verification.all.auto.verification.interceptor.AutoVerificationInterceptor
import com.sinch.verification.all.auto.verification.interceptor.SubCodeInterceptionListener
import com.sinch.verification.core.config.method.AutoInterceptedVerificationMethod
import com.sinch.verification.core.config.method.VerificationMethodCreator
import com.sinch.verification.core.initiation.InitiationApiCallback
import com.sinch.verification.core.initiation.VerificationIdentity
import com.sinch.verification.core.initiation.response.EmptyInitializationListener
import com.sinch.verification.core.internal.Verification
import com.sinch.verification.core.internal.VerificationMethodType
import com.sinch.verification.core.internal.utils.enqueue
import com.sinch.verification.core.verification.VerificationApiCallback
import com.sinch.verification.core.verification.model.VerificationSourceType
import com.sinch.verification.core.verification.response.EmptyVerificationListener
import com.sinch.verification.core.verification.response.VerificationListener
import com.sinch.verification.sms.initialization.SmsOptions
import com.sinch.verification.utils.MAX_TIMEOUT

typealias  EmptyAutoInitializationListener = EmptyInitializationListener<AutoInitializationResponseData>
typealias  SimpleInitializationAutoApiCallback = InitiationApiCallback<AutoInitializationResponseData>

class AutoVerificationMethod private constructor(
    private val config: AutoVerificationConfig,
    private val initializationListener: AutoInitializationListener = EmptyAutoInitializationListener(),
    verificationListener: VerificationListener = EmptyVerificationListener()
) : AutoInterceptedVerificationMethod<AutoVerificationService, AutoVerificationInterceptor>(
    config,
    verificationListener
), SubCodeInterceptionListener {

    private val requestDataData: AutoInitializationData
        get() =
            AutoInitializationData(
                identity = VerificationIdentity(config.number),
                honourEarlyReject = config.honourEarlyReject,
                custom = config.custom,
                reference = config.reference,
                metadata = config.metadataFactory.create(),
                smsOptions = SmsOptions(applicationHash = config.appHash)
            )

    override var codeInterceptor: AutoVerificationInterceptor? = null

    private var autoInitializationResponseData: AutoInitializationResponseData? = null
    private var currentVerificationMethod: VerificationMethodType? = null

    override fun onInitiate() {
        verificationService.initializeVerification(requestDataData)
            .enqueue(
                retrofit = retrofit,
                apiCallback = SimpleInitializationAutoApiCallback(
                    listener = initializationListener,
                    statusListener = this,
                    successCallback = {
                        this.autoInitializationResponseData = it
                        this.initializeInterceptors()
                        this.proceedToNextVerificationMethod()
                    }
                ))
    }

    private fun initializeInterceptors() {
        autoInitializationResponseData?.let {
            codeInterceptor = AutoVerificationInterceptor(
                context = config.globalConfig.context,
                autoInitializationResponseData = it,
                interceptionTimeout = Long.MAX_TIMEOUT,
                subCodeInterceptionListener = this,
                autoCodeInterceptionListener = this
            )
        }
        codeInterceptor?.start()
    }

    private fun proceedToNextVerificationMethod() {
        currentVerificationMethod =
            autoInitializationResponseData?.autoDetails?.methodAfter(currentVerificationMethod)
        if (currentVerificationMethod == VerificationMethodType.SEAMLESS || currentVerificationMethod == null) {
            tryVerifySeamlessly()
        }
    }

    private fun tryVerifySeamlessly() {
        autoInitializationResponseData?.seamlessDetails?.let {
            verificationService.verifySeamless(it.targetUri)
                .enqueue(
                    retrofit = retrofit,
                    apiCallback = VerificationApiCallback(
                        listener = verificationListener,
                        verificationStateListener = this
                    )
                )
        }
    }

    override fun onSubCodeIntercepted(
        code: String,
        method: VerificationMethodType,
        source: VerificationSourceType
    ) {
        currentVerificationMethod = method
        verify(code, source)
    }

    override fun onSubCodeInterceptionError(e: Throwable, method: VerificationMethodType) {
        proceedToNextVerificationMethod()
    }

    override fun onSubCodeInterceptionStopped(method: VerificationMethodType) {}

    override fun onVerify(verificationCode: String, sourceType: VerificationSourceType) {
        val currentVerificationMethod = currentVerificationMethod ?: return
        val autoInitializationResponseData = autoInitializationResponseData ?: return
        val currentSubVerificationId =
            autoInitializationResponseData.subVerificationDetails(currentVerificationMethod)?.subVerificationId
                ?: return

        val verificationData =
            AutoVerificationData.create(currentVerificationMethod, sourceType, verificationCode)

        verificationService.verifyById(
            subVerificationId = currentSubVerificationId,
            data = verificationData
        ).enqueue(retrofit, VerificationApiCallback(verificationListener, this))

    }

    override fun onCodeIntercepted(code: String, source: VerificationSourceType) {
        //We should not get any callbacks here. In case of auto verification only sub interceptors listeners should be notified.
    }

    override fun onCodeInterceptionError(e: Throwable) {}

    /**
     * Builder implementing fluent builder pattern to create [AutoVerificationMethod] objects.
     */
    class Builder private constructor() :
        VerificationMethodCreator<AutoInitializationListener>,
        AutoVerificationConfigSetter {

        private val logger = logger()

        companion object {

            /**
             * Instance of builder that should be used to create [AutoVerificationMethod] object.
             */
            @JvmStatic
            val instance: AutoVerificationConfigSetter
                get() = Builder()

            operator fun invoke() = instance

        }

        private var initializationListener: AutoInitializationListener =
            EmptyAutoInitializationListener()
        private var verificationListener: VerificationListener = EmptyVerificationListener()

        private lateinit var config: AutoVerificationConfig

        /**
         * Assigns config to the builder.
         * @param config Reference to callout configuration object.
         * @return Instance of builder with assigned config.
         */
        override fun config(config: AutoVerificationConfig): VerificationMethodCreator<AutoInitializationListener> =
            apply {
                this.config = config
            }

        /**
         * Assigns verification listener to the builder.
         * @param verificationListener Listener to be notified about the verification process result.
         * @return Instance of builder with assigned verification listener.
         */
        override fun verificationListener(verificationListener: VerificationListener): VerificationMethodCreator<AutoInitializationListener> =
            apply {
                this.verificationListener = verificationListener
            }

        /**
         * Assigns initialization listener to the builder.
         * @param initializationListener Listener to be notified about verification initiation result.
         * @return Instance of builder with assigned initialization listener.
         */
        override fun initializationListener(initializationListener: AutoInitializationListener): VerificationMethodCreator<AutoInitializationListener> =
            apply {
                this.initializationListener = initializationListener
            }

        /**
         * Builds [AutoVerificationMethod] instance.
         * @return [Verification] instance with previously defined parameters.
         */
        override fun build(): Verification {
            return AutoVerificationMethod(
                config = config,
                initializationListener = initializationListener,
                verificationListener = verificationListener
            ).also {
                logger.debug("Created AutoVerificationMethod with config: $config")
            }
        }

    }

}