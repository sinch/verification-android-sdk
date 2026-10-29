package com.sinch.verification.seamless

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.TelephonyNetworkSpecifier
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.telephony.SubscriptionManager
import androidx.core.app.ActivityCompat
import com.sinch.logging.logger
import com.sinch.verification.core.config.method.VerificationMethod
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
import com.sinch.verification.seamless.SeamlessVerificationMethod.Companion.MAX_REQUEST_DELAY
import com.sinch.verification.seamless.config.SeamlessVerificationConfig
import com.sinch.verification.seamless.digitalcredential.DigitalCredentialVerification
import com.sinch.verification.seamless.initialization.SeamlessInitializationDetails
import com.sinch.verification.seamless.initialization.SeamlessInitializationListener
import com.sinch.verification.seamless.initialization.SeamlessInitiationData
import com.sinch.verification.seamless.initialization.SeamlessInitiationResponseData
import com.sinch.verification.utils.permission.Permission
import com.sinch.verification.utils.permission.PermissionUtils
import retrofit2.Retrofit

typealias EmptySeamlessInitializationListener = EmptyInitializationListener<SeamlessInitiationResponseData>
typealias SimpleInitializationSeamlessApiCallback = InitiationApiCallback<SeamlessInitiationResponseData>

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

    private val requestData: SeamlessInitiationData
        get() =
            SeamlessInitiationData(
                identity = VerificationIdentity(
                    config.number ?: throw IllegalArgumentException(
                        "Phone number to verify not set"
                    )
                ),
                honourEarlyReject = config.honourEarlyReject,
                custom = config.custom,
                reference = config.reference,
                metadata = config.metadataFactory.create()
            )

    /**
     * Verification strategy resolved from the initiation response. Selected in [onInitiate] and
     * then driven through the standard [verify] / [onVerify] channel.
     */
    private var strategy: SeamlessVerificationStrategy? = null

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
                    successCallback = { responseData ->
                        val details = responseData.details
                        if (details.isV2) {
                            val verificationId = id
                            if (verificationId == null) {
                                failVerification("Missing verification id for the v2 seamless flow")
                            } else {
                                val androidAppUrl = details.androidAppUrl
                                if (androidAppUrl != null) {
                                    startStrategy(
                                        selectedStrategy = createAppUrlStrategy(details, verificationId),
                                        payload = androidAppUrl
                                    )
                                } else {
                                    val vpResponse = details.vpResponse
                                    if (vpResponse == null) {
                                        failVerification(
                                            "v2 verification response is missing androidAppUrl and vpResponse"
                                        )
                                    } else {
                                        startStrategy(
                                            createDigitalCredentialVerification(details.nonce, verificationId),
                                            vpResponse
                                        )
                                    }
                                }
                            }
                        } else {
                            startStrategy(CellularRedirectStrategy(), details.targetUri.orEmpty())
                        }
                    }
                ))
    }

    private fun startStrategy(selectedStrategy: SeamlessVerificationStrategy, payload: String) {
        strategy = selectedStrategy
        update(VerificationState.Verification(VerificationStateStatus.ONGOING))
        selectedStrategy.start(payload)
    }

    private fun failVerification(
        message: String,
        error: Throwable = VerificationException(message)
    ) {
        logger.error(message, error)
        update(VerificationState.Verification(VerificationStateStatus.ERROR))
        verificationListener.onVerificationFailed(error)
    }

    override fun onVerify(
        verificationCode: String,
        sourceType: VerificationSourceType,
        method: VerificationMethodType?
    ) {
        val currentStrategy = strategy
        if (currentStrategy != null) {
            currentStrategy.handleCallback(verificationCode)
        } else {
            // No local initiation (skipLocalInitialization flow): the code is the v1 targetUri
            // returned by an externally initiated verification.
            startStrategy(CellularRedirectStrategy(), verificationCode)
        }
    }

    override fun stop() {
        strategy?.stop()
        super.stop()
    }

    override fun onCodeIntercepted(code: String, source: VerificationSourceType) {}

    override fun onCodeInterceptionError(e: Throwable) {
        failVerification("Code interception failed", e)
    }

    private fun createDigitalCredentialVerification(
        nonce: String?,
        verificationId: String
    ): DigitalCredentialVerification =
        DigitalCredentialVerification(
            nonce = nonce,
            globalConfig = globalConfig,
            activity = config.activityContext,
            onLocalFailure = { error ->
                failVerification(
                    error.message ?: "Digital credential verification failed",
                    error
                )
            },
            onLocalSuccess = { operatorToken ->
                verificationService.verifySeamlessWithCredential(
                    SeamlessCallbackBody(state = verificationId, operatorToken = operatorToken)
                ).enqueue(
                    retrofit = retrofit,
                    apiCallback = VerificationApiCallback(
                        listener = verificationListener,
                        verificationStateListener = this
                    )
                )
            }
        )

    private fun createAppUrlStrategy(
        details: SeamlessInitializationDetails,
        verificationId: String
    ): AppUrlVerificationStrategy =
        AppUrlVerificationStrategy(
            globalConfig = globalConfig,
            appInfoJwt = details.appInfoJwt,
            appInfoJwtQueryParameterName = details.appInfoJwtQueryParameterName,
            appCallbackQueryParameterName = details.appCallbackQueryParameterName,
            onLocalFailure = { error ->
                failVerification(
                    error.message ?: "App url verification failed",
                    error
                )
            },
            onLocalSuccess = { operatorToken ->
                verificationService.verifySeamlessWithCredential(
                    SeamlessCallbackBody(state = verificationId, operatorToken = operatorToken)
                ).enqueue(
                    retrofit = retrofit,
                    apiCallback = VerificationApiCallback(
                        listener = verificationListener,
                        verificationStateListener = this
                    )
                )
            }
        )

    /**
     * v1 strategy. Forces a GET request on the returned `targetUri` over the cellular network so
     * that the mobile operator can seamlessly verify the number, falling back to the default
     * network after [MAX_REQUEST_DELAY] if no cellular network becomes available.
     */
    @SuppressLint("NewApi")
    private inner class CellularRedirectStrategy : SeamlessVerificationStrategy {

        private val connectivityManager by lazy {
            config.globalConfig.context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        }

        private val subscriptionManager by lazy {
            config.globalConfig.context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
        }

        private val networkRequestHandler = Handler(Looper.getMainLooper())
        private var lastNetworkCallback: ConnectivityManager.NetworkCallback? = null

        override fun start(payload: String) {
            val targetUri = payload

            val cellularRequest = NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .setSpecificTelephonyNetworkIfCapable()
                .build()

            lastNetworkCallback = object : ConnectivityManager.NetworkCallback() {

                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    logger.debug("Cellular network available $network")
                    networkRequestHandler.removeCallbacksAndMessages(null)
                    networkRequestHandler.post {
                        executeVerificationRequest(
                            targetUri,
                            globalConfig.socketFactoryRetrofit(network.socketFactory)
                        )
                    }
                }

                override fun onLosing(network: Network, maxMsToLive: Int) {
                    super.onLosing(network, maxMsToLive)
                    logger.debug("Cellular onLosing")
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    logger.debug("Cellular onLost")
                }

                override fun onBlockedStatusChanged(network: Network, blocked: Boolean) {
                    super.onBlockedStatusChanged(network, blocked)
                    logger.debug("onBlockedStatusChanged")
                }

                override fun onCapabilitiesChanged(
                    network: Network,
                    networkCapabilities: NetworkCapabilities
                ) {
                    super.onCapabilitiesChanged(network, networkCapabilities)
                    logger.debug("onCapabilitiesChanged")
                }

                override fun onLinkPropertiesChanged(network: Network, linkProperties: LinkProperties) {
                    super.onLinkPropertiesChanged(network, linkProperties)
                    logger.debug("onLinkPropertiesChanged")
                }

                override fun onUnavailable() {
                    super.onUnavailable()
                    logger.error("Cellular network not available")
                    networkRequestHandler.removeCallbacksAndMessages(null)
                    networkRequestHandler.post {
                        executeVerificationRequest(targetUri)
                    }
                }
            }

            lastNetworkCallback?.let {
                connectivityManager.requestNetwork(cellularRequest, it)
            }

            networkRequestHandler.postDelayed({
                executeVerificationRequest(targetUri)
            }, MAX_REQUEST_DELAY)
        }

        override fun stop() {
            networkRequestHandler.removeCallbacksAndMessages(null)
            resetNetworkBindings()
        }

        private fun executeVerificationRequest(
            targetUri: String,
            specificRetrofit: Retrofit? = null
        ) {
            val usedRetrofit = specificRetrofit ?: retrofit

            val service: SeamlessVerificationService =
                specificRetrofit?.create(SeamlessVerificationService::class.java) ?: verificationService

            service.verifySeamless(targetUri)
                .enqueue(
                    retrofit = usedRetrofit,
                    apiCallback = VerificationApiCallback(
                        listener = verificationListener,
                        verificationStateListener = this@SeamlessVerificationMethod,
                        beforeResultHandledCallback = this::resetNetworkBindings
                    )
                )
        }

        private fun resetNetworkBindings() {
            val callback = lastNetworkCallback ?: return
            lastNetworkCallback = null
            logger.debug("Resetting network bindings")
            connectivityManager.unregisterNetworkCallback(callback)
        }

        private fun NetworkRequest.Builder.setSpecificTelephonyNetworkIfCapable(): NetworkRequest.Builder {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                logger.debug("Cannot set specific telephony network in the request as device does not run Android 10+")
                return this
            }
            if (ActivityCompat.checkSelfPermission(
                    globalConfig.context,
                    Manifest.permission.READ_PHONE_STATE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                logger.debug("Cannot set specific telephony network in the request as the app doesn't have required permissions")
                return this
            }
            @Suppress("DEPRECATION") val usedNumberSubId = subscriptionManager.activeSubscriptionInfoList?.firstOrNull {
                (it.number == config.number) && config.number != null
            }?.subscriptionId
            return if (usedNumberSubId == null) {
                logger.debug(
                    "Cannot set specific telephony network request, did not find subscription with number " +
                        "${config.number}"
                )
                this
            } else {
                logger.debug("Setting TelephonyNetworkSpecifier with subscription id $usedNumberSubId")
                setNetworkSpecifier(
                    TelephonyNetworkSpecifier.Builder().setSubscriptionId(usedNumberSubId).build()
                )
            }
        }
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
