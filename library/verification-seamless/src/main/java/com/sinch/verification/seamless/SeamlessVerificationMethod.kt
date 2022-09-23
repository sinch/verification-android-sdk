package com.sinch.verification.seamless

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.*
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.telephony.SubscriptionManager
import androidx.core.app.ActivityCompat
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
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
import com.sinch.verification.core.verification.VerificationService
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
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import kotlin.math.log

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

    private val subscriptionManager by lazy {
        config.globalConfig.context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
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

    private var lastNetworkCallback: ConnectivityManager.NetworkCallback? = null

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

    @SuppressLint("NewApi")
    override fun onVerify(
        verificationCode: String,
        sourceType: VerificationSourceType,
        method: VerificationMethodType?
    ) {

        val cellularRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .setSpecificTelephonyNetworkIfCapable()
            .build()

        lastNetworkCallback = object : ConnectivityManager.NetworkCallback() {

            override fun onAvailable(network: Network) {
                super.onAvailable(network)

                network.socketFactory
//                globalConfig.retrofit.newBuilder().client(globalConfig.retrofit.)
//                val retrofit2 = Retrofit.Builder()
//                    .baseUrl("http://localhost/")
//                    .client(OkHttpClient.Builder().socketFactory(network.socketFactory)
//                        .build())
//                    .addConverterFactory(
//                        Json {
//                            encodeDefaults = true
//                            ignoreUnknownKeys = true
//                        }
//                            .asConverterFactory("application/json".toMediaType())
//                    )
//                    .build()


                logger.debug("Cellular network available $network")
                networkRequestHandler.removeCallbacksAndMessages(null)
                networkRequestHandler.post {
                    val returned = connectivityManager.changeProcessNetworkTo(network)
                    logger.debug("changeProcessNetworkTo returned $returned")
                    executeVerificationRequest(verificationCode)
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
                    executeVerificationRequest(verificationCode)
                }
            }
        }

        lastNetworkCallback?.let {
            connectivityManager.requestNetwork(cellularRequest, it)
        }

        networkRequestHandler.postDelayed({
            executeVerificationRequest(verificationCode)
        }, MAX_REQUEST_DELAY)
    }

    override fun onCodeIntercepted(code: String, source: VerificationSourceType) {}

    override fun onCodeInterceptionError(e: Throwable) {
        verificationListener.onVerificationFailed(e)
    }

    private fun executeVerificationRequest(verificationCode: String, specificRetrofit: Retrofit? = null) {
        val usedRetrofit = specificRetrofit ?: retrofit

        val service:SeamlessVerificationService  = if(specificRetrofit != null) specificRetrofit.create(SeamlessVerificationService::class.java) else  verificationService

        service.verifySeamless(verificationCode)
            .enqueue(
                retrofit = usedRetrofit,
                apiCallback = VerificationApiCallback(
                    listener = verificationListener,
                    verificationStateListener = this,
                    beforeResultHandledCallback = this::resetNetworkBindings
                )
            )
    }

    private fun resetNetworkBindings() {
        logger.debug("Resetting network bindings")
        connectivityManager.changeProcessNetworkTo(null)
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
        val allSubs = subscriptionManager.activeSubscriptionInfoList
        val subData = SubscriptionManager.getActiveDataSubscriptionId()
        val subDataDefault = SubscriptionManager.getDefaultDataSubscriptionId()
        val activePhone = SubscriptionManager.getDefaultVoiceSubscriptionId()

        logger.debug("Subscription id uses for cellular is $subData $subDataDefault")
        @Suppress("DEPRECATION") val usedNumberSubId = subscriptionManager.activeSubscriptionInfoList.firstOrNull {
            it.number == requestData.identity.endpoint
        }?.subscriptionId
        return if (usedNumberSubId == null) {
            logger.debug("Cannot set specific telephony network request, did not find subscription with number ${requestData.identity.endpoint}")
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