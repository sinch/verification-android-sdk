package com.sinch.verification.seamless

import com.sinch.logging.logger
import com.sinch.verification.seamless.config.SeamlessVerificationConfig
import com.sinch.verification.seamless.initialization.SeamlessInitializationListener
import com.sinch.verification.seamless.initialization.SeamlessInitiationData
import com.sinch.verification.seamless.initialization.SeamlessInitiationResponseData
import com.sinch.verificationcore.config.method.VerificationMethod
import com.sinch.verificationcore.config.method.VerificationMethodCreator
import com.sinch.verificationcore.initiation.InitiationApiCallback
import com.sinch.verificationcore.initiation.VerificationIdentity
import com.sinch.verificationcore.initiation.response.EmptyInitializationListener
import com.sinch.verificationcore.internal.Verification
import com.sinch.verificationcore.internal.utils.enqueue
import com.sinch.verificationcore.verification.VerificationApiCallback
import com.sinch.verificationcore.verification.VerificationSourceType
import com.sinch.verificationcore.verification.response.EmptyVerificationListener
import com.sinch.verificationcore.verification.response.VerificationListener

typealias  EmptySeamlessInitializationListener = EmptyInitializationListener<SeamlessInitiationResponseData>
typealias  SimpleInitializationSeamlessApiCallback = InitiationApiCallback<SeamlessInitiationResponseData>

/**
 * [Verification] that uses Seamless method for verification.
 * @param config Reference to seamless configuration object.
 * @param initializationListener Listener to be notified about verification initiation result.
 * @param verificationListener Listener to be notified about the verification process result.
 */
class SeamlessVerificationMethod private constructor(
    private val config: SeamlessVerificationConfig,
    private val initializationListener: SeamlessInitializationListener = EmptySeamlessInitializationListener(),
    verificationListener: VerificationListener = EmptyVerificationListener()

) : VerificationMethod<SeamlessVerificationService>(config, verificationListener) {

    private val requestDataData: SeamlessInitiationData
        get() =
            SeamlessInitiationData(
                identity = VerificationIdentity(config.number),
                honourEarlyReject = config.honourEarlyReject,
                custom = config.custom,
                reference = config.reference,
                metadata = config.metadataFactory.create()
            )

    override fun onInitiate() {
        verificationService.initializeVerification(requestDataData)
            .enqueue(
                retrofit = retrofit,
                apiCallback = SimpleInitializationSeamlessApiCallback(
                    listener = initializationListener,
                    statusListener = this,
                    successCallback = { verify(it.details.targetUri) }
                ))
    }

    override fun onVerify(verificationCode: String, sourceType: VerificationSourceType) {
        verificationService.verifySeamless(verificationCode)
            .enqueue(
                retrofit = retrofit,
                apiCallback = VerificationApiCallback(verificationListener, this)
            )
    }

    override fun onCodeIntercepted(code: String, source: VerificationSourceType) {}

    override fun onCodeInterceptionError(e: Throwable) {
        verificationListener.onVerificationFailed(e)
    }

    /**
     * Builder implementing fluent builder pattern to create [SeamlessVerificationMethod] objects.
     */
    class Builder private constructor() :
        VerificationMethodCreator<SeamlessInitializationListener>,
        SeamlessVerificationConfigSetter {

        private val logger = logger()

        companion object {

            /**
             * Instance of builder that should be used to create [SeamlessVerificationMethod] object.
             */
            @JvmStatic
            val instance: SeamlessVerificationConfigSetter
                get() = Builder()

            operator fun invoke() = instance

        }

        private var initializationListener: SeamlessInitializationListener =
            EmptySeamlessInitializationListener()
        private var verificationListener: VerificationListener = EmptyVerificationListener()

        private lateinit var config: SeamlessVerificationConfig

        /**
         * Assigns config to the builder.
         * @param config Reference to callout configuration object.
         * @return Instance of builder with assigned config.
         */
        override fun config(config: SeamlessVerificationConfig): VerificationMethodCreator<SeamlessInitializationListener> =
            apply {
                this.config = config
            }

        /**
         * Assigns verification listener to the builder.
         * @param verificationListener Listener to be notified about the verification process result.
         * @return Instance of builder with assigned verification listener.
         */
        override fun verificationListener(verificationListener: VerificationListener): VerificationMethodCreator<SeamlessInitializationListener> =
            apply {
                this.verificationListener = verificationListener
            }

        /**
         * Assigns initialization listener to the builder.
         * @param initializationListener Listener to be notified about verification initiation result.
         * @return Instance of builder with assigned initialization listener.
         */
        override fun initializationListener(initializationListener: SeamlessInitializationListener): VerificationMethodCreator<SeamlessInitializationListener> =
            apply {
                this.initializationListener = initializationListener
            }

        /**
         * Builds [SeamlessVerificationMethod] instance.
         * @return [Verification] instance with previously defined parameters.
         */
        override fun build(): Verification {
            return SeamlessVerificationMethod(
                config = config,
                initializationListener = initializationListener,
                verificationListener = verificationListener
            ).also {
                logger.debug("Created SeamlessVerificationMethod with config: $config")
            }
        }

    }

}