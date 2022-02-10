package com.sinch.verification.seamless

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Handler
import android.os.Looper
import com.sinch.logging.logger
import com.sinch.verification.core.config.method.VerificationMethod
import com.sinch.verification.core.config.method.VerificationMethodCreator
import com.sinch.verification.core.initiation.InitiationApiCallback
import com.sinch.verification.core.initiation.VerificationIdentity
import com.sinch.verification.core.initiation.response.EmptyInitializationListener
import com.sinch.verification.core.internal.Verification
import com.sinch.verification.core.internal.VerificationMethodType
import com.sinch.verification.core.internal.error.VerificationException
import com.sinch.verification.core.internal.utils.enqueue
import com.sinch.verification.core.verification.VerificationApiCallback
import com.sinch.verification.core.verification.model.VerificationSourceType
import com.sinch.verification.core.verification.response.EmptyVerificationListener
import com.sinch.verification.core.verification.response.VerificationListener
import com.sinch.verification.seamless.config.SeamlessVerificationConfig
import com.sinch.verification.seamless.initialization.SeamlessInitializationListener
import com.sinch.verification.seamless.initialization.SeamlessInitiationData
import com.sinch.verification.seamless.initialization.SeamlessInitiationResponseData
import com.sinch.verification.utils.changeProcessNetworkTo
import com.sinch.verification.utils.permission.Permission
import com.sinch.verification.utils.permission.PermissionUtils

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

    companion object {
        const val MAX_REQUEST_DELAY = 3000L // in ms
    }

    private val connectivityManager by lazy {
        config.globalConfig.context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    private val networkRequestHandler = Handler(Looper.getMainLooper())

    private val requestData: SeamlessInitiationData
        get() =
            SeamlessInitiationData(
                identity = VerificationIdentity(config.number),
                honourEarlyReject = config.honourEarlyReject,
                custom = config.custom,
                reference = config.reference,
                metadata = config.metadataFactory.create()
            )

    override fun onPreInitiate(): Boolean {
        if (!PermissionUtils.isPermissionGranted(
                globalConfig.context,
                Permission.CHANGE_NETWORK_STATE
            )
        ) {
            initializationListener.onInitializationFailed(VerificationException("Missing ${Permission.CHANGE_NETWORK_STATE}"))
        }
        return true
    }

    override fun onInitiate() {
        logger.info("onInitiate called with requestData: $requestData")
        verificationService.initializeVerification(requestData)
            .enqueue(
                retrofit = retrofit,
                apiCallback = SimpleInitializationSeamlessApiCallback(
                    listener = initializationListener,
                    statusListener = this,
                    successCallback = {
                        verify(it.details.targetUri.orEmpty())
                    }
                ))
    }

    override fun onVerify(
        verificationCode: String,
        sourceType: VerificationSourceType,
        method: VerificationMethodType?
    ) {

        val cellularRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.requestNetwork(
            cellularRequest,
            object : ConnectivityManager.NetworkCallback() {

                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    logger.debug("Cellular network available $network")
                    networkRequestHandler.removeCallbacksAndMessages(null)
                    networkRequestHandler
                    networkRequestHandler.post {
                        connectivityManager.changeProcessNetworkTo(network)
                        executeVerificationRequest(verificationCode)
                    }
                }

                override fun onUnavailable() {
                    super.onUnavailable()
                    logger.error("Cellular network not available")
                    networkRequestHandler.removeCallbacksAndMessages(null)
                    networkRequestHandler.post {
                        executeVerificationRequest(verificationCode)
                    }
                }
            })
        networkRequestHandler.postDelayed({
            executeVerificationRequest(verificationCode)
        }, MAX_REQUEST_DELAY)
    }

    override fun onCodeIntercepted(code: String, source: VerificationSourceType) {}

    override fun onCodeInterceptionError(e: Throwable) {
        verificationListener.onVerificationFailed(e)
    }

    private fun executeVerificationRequest(verificationCode: String) {
        verificationService.verifySeamless(verificationCode)
            .enqueue(
                retrofit = retrofit,
                apiCallback = VerificationApiCallback(
                    listener = verificationListener,
                    verificationStateListener = this,
                    beforeResultHandledCallback = this::resetNetworkBindings
                )
            )
    }

    private fun resetNetworkBindings() {
        connectivityManager.changeProcessNetworkTo(null)
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