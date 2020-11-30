package com.sinch.verification.callout

import com.sinch.logging.logger
import com.sinch.verification.callout.config.CalloutVerificationConfig
import com.sinch.verification.callout.initialization.CalloutInitializationListener
import com.sinch.verification.callout.initialization.CalloutInitializationResponseData
import com.sinch.verification.callout.initialization.CalloutVerificationInitializationData
import com.sinch.verification.callout.verification.CalloutVerificationData
import com.sinch.verification.callout.verification.CalloutVerificationDetails
import com.sinch.verification.callout.verification.interceptor.CalloutInterceptor
import com.sinch.verification.core.config.method.VerificationMethod
import com.sinch.verification.core.config.method.VerificationMethodCreator
import com.sinch.verification.core.initiation.InitiationApiCallback
import com.sinch.verification.core.initiation.VerificationIdentity
import com.sinch.verification.core.initiation.response.EmptyInitializationListener
import com.sinch.verification.core.internal.Verification
import com.sinch.verification.core.internal.utils.enqueue
import com.sinch.verification.core.verification.VerificationApiCallback
import com.sinch.verification.core.verification.VerificationSourceType
import com.sinch.verification.core.verification.response.EmptyVerificationListener
import com.sinch.verification.core.verification.response.VerificationListener
import com.sinch.verification.utils.MAX_TIMEOUT

typealias EmptyCalloutInitializationListener = EmptyInitializationListener<CalloutInitializationResponseData>
typealias SimpleInitializationCalloutApiCallback = InitiationApiCallback<CalloutInitializationResponseData>

/**
 * [Verification] that uses callouts to verify user's phone number. After initiated this
 * @param config Reference to callout configuration object.
 * @param initializationListener Listener to be notified about verification initiation result.
 * @param verificationListener Listener to be notified about the verification process result.
 */
class CalloutVerificationMethod private constructor(
    private val config: CalloutVerificationConfig,
    private val initializationListener: CalloutInitializationListener = EmptyCalloutInitializationListener(),
    verificationListener: VerificationListener = EmptyVerificationListener()
) : VerificationMethod<CalloutVerificationService>(config, verificationListener) {

    private var calloutInterceptor: CalloutInterceptor? = null

    private val requestDataData: CalloutVerificationInitializationData
        get() =
            CalloutVerificationInitializationData(
                identity = VerificationIdentity(config.number),
                honourEarlyReject = config.honourEarlyReject,
                custom = config.custom,
                reference = config.reference,
                metadata = config.metadataFactory.create()
            )

    override fun onInitiate() {
        verificationService.initializeVerification(requestDataData).enqueue(
            retrofit = retrofit,
            apiCallback = SimpleInitializationCalloutApiCallback(
                listener = initializationListener,
                statusListener = this,
                successCallback = { initializeInterceptor() })
        )
    }

    override fun onVerify(verificationCode: String, sourceType: VerificationSourceType) {
        verificationService.verifyNumber(
            number = config.number,
            data = CalloutVerificationData(details = CalloutVerificationDetails(code = verificationCode))
        ).enqueue(
            retrofit = retrofit,
            apiCallback = VerificationApiCallback(verificationListener, this)
        )
    }

    override fun onCodeIntercepted(code: String, source: VerificationSourceType) {}

    override fun onCodeInterceptionError(e: Throwable) {
        verificationListener.onVerificationFailed(e)
    }

    private fun initializeInterceptor() {
        calloutInterceptor = CalloutInterceptor(
            interceptionTimeout = Long.MAX_TIMEOUT,
            interceptionListener = this
        ).apply {
            start()
        }
    }

    /**
     * Builder implementing fluent builder pattern to create [CalloutVerificationMethod] objects.
     */
    class Builder private constructor() :
        VerificationMethodCreator<CalloutInitializationListener>,
        CalloutVerificationConfigSetter {

        private val logger = logger()

        companion object {

            /**
             * Instance of builder that should be used to create [CalloutVerificationMethod] object.
             */
            @JvmStatic
            val instance: CalloutVerificationConfigSetter
                get() = Builder()

            operator fun invoke() = instance

        }

        private var initializationListener: CalloutInitializationListener =
            EmptyCalloutInitializationListener()
        private var verificationListener: VerificationListener = EmptyVerificationListener()

        private lateinit var config: CalloutVerificationConfig

        /**
         * Assigns config to the builder.
         * @param config Reference to callout configuration object.
         * @return Instance of builder with assigned config.
         */
        override fun config(config: CalloutVerificationConfig): VerificationMethodCreator<CalloutInitializationListener> =
            apply {
                this.config = config
            }

        /**
         * Assigns verification listener to the builder.
         * @param verificationListener Listener to be notified about the verification process result.
         * @return Instance of builder with assigned verification listener.
         */
        override fun verificationListener(verificationListener: VerificationListener): VerificationMethodCreator<CalloutInitializationListener> =
            apply {
                this.verificationListener = verificationListener
            }

        /**
         * Assigns initialization listener to the builder.
         * @param initializationListener Listener to be notified about verification initiation result.
         * @return Instance of builder with assigned initialization listener.
         */
        override fun initializationListener(initializationListener: CalloutInitializationListener): VerificationMethodCreator<CalloutInitializationListener> =
            apply {
                this.initializationListener = initializationListener
            }

        /**
         * Builds [CalloutVerificationMethod] instance.
         * @return [Verification] instance with previously defined parameters.
         */
        override fun build(): Verification {
            return CalloutVerificationMethod(
                config = config,
                initializationListener = initializationListener,
                verificationListener = verificationListener
            ).also {
                logger.debug("Created CalloutVerificationMethod with config: $config")
            }
        }

    }

}