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
import android.telephony.TelephonyManager
import androidx.core.app.ActivityCompat
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
import com.sinch.verification.seamless.util.SeamlessRetrofitProvider
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

    private lateinit var seamlessRetrofit: Retrofit

    private val connectivityManager by lazy {
        config.globalConfig.context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    private val subscriptionManager by lazy {
        config.globalConfig.context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
    }

    private val networkRequestHandler = Handler(Looper.getMainLooper())
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

    private var lastNetworkCallback: ConnectivityManager.NetworkCallback? = null

    @SuppressLint("MissingPermission")
    override fun onPreInitiate(): Boolean {
        if (!PermissionUtils.isPermissionGranted(
                globalConfig.context,
                Permission.CHANGE_NETWORK_STATE
            )
        ) {
            initializationListener.onInitializationFailed(VerificationException("Missing ${Permission.CHANGE_NETWORK_STATE}"))
            return false
        }
        val tm: TelephonyManager =
            globalConfig.context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager;

        // Check if mobile data is connected for Oreo & Above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
            && PermissionUtils.isPermissionGranted(
                globalConfig.context,
                Permission.CHANGE_NETWORK_STATE
            )
        ) {
            if (!tm.isDataEnabled) {
                initializationListener.onInitializationFailed(VerificationException("Cellular network not available"))
                return false
            }
        }
        return true
    }

    override fun onInitiate() {
        logger.info("onInitiate called with requestData: $requestData")

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

                seamlessRetrofit = SeamlessRetrofitProvider.buildVerificationServiceRetrofitWithSocket(config, socketFactory = network.socketFactory)

                networkRequestHandler.post {
                    initiateVerificationRequest()
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
                verificationListener.onVerificationFailed(VerificationException("Cellular network not available"))
            }
        }

        lastNetworkCallback?.let {
            connectivityManager.requestNetwork(cellularRequest, it)
        }

        networkRequestHandler.postDelayed({
            lastNetworkCallback?.let {
                connectivityManager.unregisterNetworkCallback(it)
            }
            networkRequestHandler.removeCallbacksAndMessages(null)
            verificationListener.onVerificationFailed(VerificationException("Cellular network not available"))
        }, MAX_REQUEST_DELAY)
    }

    override fun onCodeIntercepted(code: String, source: VerificationSourceType) {}

    override fun onCodeInterceptionError(e: Throwable) {
        verificationListener.onVerificationFailed(e)
    }

    private fun initiateVerificationRequest() {
        val service = seamlessRetrofit.create(SeamlessVerificationService::class.java)

        service.initializeVerification(requestData)
            .enqueue(
                retrofit = seamlessRetrofit,
                apiCallback = SimpleInitializationSeamlessApiCallback(
                    listener = initializationListener,
                    statusListener = this,
                    successCallback = {
                        verify(it.details.targetUri.orEmpty())
                    }
                ))
    }

    @SuppressLint("NewApi")
    override fun onVerify(
        verificationCode: String,
        sourceType: VerificationSourceType,
        method: VerificationMethodType?
    ) {

        val seamlessVerificationRetrofit = SeamlessRetrofitProvider.buildSeamlessVerificationRetrofit(seamlessRetrofit, config.number)

        val service: SeamlessVerificationService =
            seamlessVerificationRetrofit.create(SeamlessVerificationService::class.java)

        service.verifySeamless(verificationCode)
            .enqueue(
                retrofit = seamlessVerificationRetrofit,
                apiCallback = VerificationApiCallback(
                    listener = verificationListener,
                    verificationStateListener = this,
                    beforeResultHandledCallback = this::resetNetworkBindings
                )
            )
    }

    private fun resetNetworkBindings() {
        logger.debug("Resetting network bindings")
        lastNetworkCallback?.let {
            connectivityManager.unregisterNetworkCallback(it)
        }
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