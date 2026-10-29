package com.sinch.verification.all.auto

import android.net.Uri
import com.sinch.logging.logger
import com.sinch.verification.all.auto.config.AutoVerificationConfig
import com.sinch.verification.all.auto.initialization.AutoInitializationData
import com.sinch.verification.all.auto.initialization.AutoInitializationListener
import com.sinch.verification.all.auto.initialization.AutoInitializationResponseData
import com.sinch.verification.all.auto.verification.AutoVerificationData
import com.sinch.verification.all.auto.verification.events.AutoVerificationEvent
import com.sinch.verification.all.auto.verification.interceptor.AutoVerificationInterceptor
import com.sinch.verification.all.auto.verification.interceptor.SubCodeInterceptionListener
import com.sinch.verification.core.config.method.AutoInterceptedVerificationMethod
import com.sinch.verification.core.config.method.VerificationMethodCreator
import com.sinch.verification.core.initiation.InitiationApiCallback
import com.sinch.verification.core.initiation.VerificationIdentity
import com.sinch.verification.core.initiation.response.EmptyInitializationListener
import com.sinch.verification.core.internal.Verification
import com.sinch.verification.core.internal.VerificationMethodType
import com.sinch.verification.core.internal.VerificationState
import com.sinch.verification.core.internal.VerificationStateStatus
import com.sinch.verification.core.internal.error.VerificationException
import com.sinch.verification.core.internal.utils.enqueue
import com.sinch.verification.core.verification.VerificationApiCallback
import com.sinch.verification.core.verification.model.VerificationSourceType
import com.sinch.verification.core.verification.response.EmptyVerificationListener
import com.sinch.verification.core.verification.response.VerificationListener
import com.sinch.verification.seamless.AppUrlVerificationStrategy
import com.sinch.verification.seamless.SeamlessCallbackBody
import com.sinch.verification.seamless.SeamlessVerificationStrategy
import com.sinch.verification.seamless.digitalcredential.DigitalCredentialVerification
import com.sinch.verification.seamless.initialization.SeamlessInitializationDetails
import com.sinch.verification.sms.initialization.SmsOptions
import com.sinch.verification.utils.MAX_TIMEOUT

typealias EmptyAutoInitializationListener = EmptyInitializationListener<AutoInitializationResponseData>
typealias SimpleInitializationAutoApiCallback = InitiationApiCallback<AutoInitializationResponseData>

class AutoVerificationMethod private constructor(
    private val config: AutoVerificationConfig,
    private val initializationListener: AutoInitializationListener = EmptyAutoInitializationListener(),
    verificationListener: VerificationListener = EmptyVerificationListener()
) : AutoInterceptedVerificationMethod<AutoVerificationService, AutoVerificationInterceptor>(
    config,
    verificationListener
), SubCodeInterceptionListener {

    private val requestData: AutoInitializationData
        get() =
            AutoInitializationData(
                identity = VerificationIdentity(config.number.orEmpty()),
                honourEarlyReject = config.honourEarlyReject,
                custom = config.custom,
                reference = config.reference,
                metadata = config.metadataFactory.create(),
                smsOptions = SmsOptions(applicationHash = config.appHash)
            )

    override var codeInterceptor: AutoVerificationInterceptor? = null

    private var autoInitializationResponseData: AutoInitializationResponseData? = null
    private var currentVerificationMethod: VerificationMethodType? = null
    private var seamlessStrategy: SeamlessVerificationStrategy? = null

    override fun onInitiate() {
        logger.info("onInitiate called with requestData: $requestData")
        verificationService.initializeVerification(requestData)
            .enqueue(
                retrofit = retrofit,
                apiCallback = SimpleInitializationAutoApiCallback(
                    listener = initializationListener,
                    statusListener = this,
                    successCallback = {
                        this.autoInitializationResponseData = it
                        this.initializeInterceptors()
                        this.proceedToNextVerificationMethod()
                        this.tryVerifySeamlessly()
                    }
                ))
    }

    private fun initializeInterceptors() {
        logger.info("Initiation interceptors for auto verification method")
        autoInitializationResponseData?.let {
            codeInterceptor = AutoVerificationInterceptor(
                context = config.globalConfig.context,
                autoInitializationResponseData = it,
                interceptionTimeout = Long.MAX_TIMEOUT,
                subCodeInterceptionListener = this,
                autoCodeInterceptionListener = this,
                verificationListener = verificationListener
            )
        }
        codeInterceptor?.start()
    }

    private fun proceedToNextVerificationMethod() {
        val previousMethod = currentVerificationMethod
        currentVerificationMethod =
            autoInitializationResponseData?.autoDetails?.methodAfter(currentVerificationMethod)
        logger.info("ProceedToNextVerificationMethod called moving from $previousMethod to $currentVerificationMethod")
    }

    private fun tryVerifySeamlessly() {
        val seamlessDetails = autoInitializationResponseData?.seamlessDetails ?: return
        logger.info("Trying to verify seamlessly")
        verificationListener.onVerificationEvent(
            AutoVerificationEvent.SubMethodVerificationCallEvent(
                VerificationMethodType.SEAMLESS
            )
        )
        if (seamlessDetails.isV2) {
            tryVerifySeamlesslyV2(seamlessDetails)
        } else {
            verificationService.verifySeamless(seamlessDetails.targetUri.orEmpty())
                .enqueue(
                    retrofit = retrofit,
                    apiCallback = VerificationApiCallback(
                        listener = verificationListener,
                        verificationStateListener = this
                    )
                )
        }
    }

    private fun tryVerifySeamlesslyV2(details: SeamlessInitializationDetails) {
        seamlessStrategy?.stop()
        val vpResponse = details.vpResponse
        val androidAppUrl = details.androidAppUrl
        val childId = details.subVerificationId
        if (childId.isNullOrEmpty()) {
            emitSeamlessSubMethodFailure(
                VerificationException("v2 auto seamless verification response is missing subVerificationId")
            )
            return
        }
        when {
            androidAppUrl != null -> {
                seamlessStrategy = createAppUrlStrategy(childId, details)
                seamlessStrategy?.start(androidAppUrl)
            }
            vpResponse != null -> {
                seamlessStrategy = createDigitalCredentialStrategy(childId, details)
                seamlessStrategy?.start(vpResponse)
            }
            else -> emitSeamlessSubMethodFailure(
                VerificationException(
                    "v2 auto seamless verification response is missing androidAppUrl and vpResponse"
                )
            )
        }
    }

    private fun createAppUrlStrategy(
        childId: String,
        details: SeamlessInitializationDetails
    ): AppUrlVerificationStrategy =
        AppUrlVerificationStrategy(
            globalConfig = globalConfig,
            appInfoJwt = details.appInfoJwt,
            appInfoJwtQueryParameterName = details.appInfoJwtQueryParameterName,
            appCallbackQueryParameterName = details.appCallbackQueryParameterName,
            onLocalFailure = { emitSeamlessSubMethodFailure(it) },
            onLocalSuccess = { operatorToken ->
                verificationService.verifySeamlessWithCredential(
                    SeamlessCallbackBody(state = childId, operatorToken = operatorToken)
                ).enqueue(
                    retrofit = retrofit,
                    apiCallback = VerificationApiCallback(
                        listener = verificationListener,
                        verificationStateListener = this
                    )
                )
            }
        )

    private fun createDigitalCredentialStrategy(
        childId: String,
        details: SeamlessInitializationDetails
    ): DigitalCredentialVerification =
        DigitalCredentialVerification(
            nonce = details.nonce,
            globalConfig = globalConfig,
            activity = config.activityContext,
            onLocalFailure = { emitSeamlessSubMethodFailure(it) },
            onLocalSuccess = { operatorToken ->
                verificationService.verifySeamlessWithCredential(
                    SeamlessCallbackBody(state = childId, operatorToken = operatorToken)
                ).enqueue(
                    retrofit = retrofit,
                    apiCallback = VerificationApiCallback(
                        listener = verificationListener,
                        verificationStateListener = this
                    )
                )
            }
        )

    private fun isHttpUrl(payload: String): Boolean {
        val scheme = Uri.parse(payload).scheme
        return scheme == "http" || scheme == "https"
    }

    private fun emitSeamlessSubMethodFailure(error: Throwable) {
        val exception = error as? Exception ?: Exception(error)
        logger.error("Seamless sub-method failed: ${exception.message}", exception)
        verificationListener.onVerificationEvent(
            AutoVerificationEvent.SubMethodFailedEvent(
                VerificationMethodType.SEAMLESS,
                exception
            )
        )
        if (verificationState.isVerified) {
            logger.info("Seamless sub-method failed after verification already succeeded; ignoring")
            return
        }
        update(VerificationState.Verification(VerificationStateStatus.ERROR))
        verificationListener.onVerificationFailed(exception)
    }

    override fun stop() {
        seamlessStrategy?.stop()
        super.stop()
    }

    override fun update(newState: VerificationState) {
        super.update(newState)
        if (newState.isVerificationProcessFinished) {
            seamlessStrategy?.stop()
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

    override fun onVerify(
        verificationCode: String,
        sourceType: VerificationSourceType,
        method: VerificationMethodType?
    ) {
        val strategy = seamlessStrategy
        if (strategy is AppUrlVerificationStrategy && isHttpUrl(verificationCode)) {
            logger.info("Routing carrier app callback to the seamless strategy")
            strategy.handleCallback(verificationCode)
            return
        }
        val verificationMethod = (method ?: currentVerificationMethod) ?: return
        val autoInitializationResponseData = autoInitializationResponseData ?: return
        val currentSubVerificationId =
            autoInitializationResponseData.subVerificationDetails(verificationMethod)?.subVerificationId
                ?: return
        val verificationData =
            AutoVerificationData.create(verificationMethod, sourceType, verificationCode)

        logger.info("onVerify called for method: $verificationMethod")
        verificationListener.onVerificationEvent(
            AutoVerificationEvent.SubMethodVerificationCallEvent(
                verificationMethod
            )
        )

        verificationService.verifyById(
            verificationId = currentSubVerificationId,
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
