package com.sinch.verification.seamless.config

import com.sinch.metadata.AndroidMetadataFactory
import com.sinch.utils.toMillisOrNull
import com.sinch.verification.seamless.SeamlessVerificationService
import com.sinch.verificationcore.BuildConfig
import com.sinch.verificationcore.config.GlobalConfigSetter
import com.sinch.verificationcore.config.NumberSetter
import com.sinch.verificationcore.config.general.GlobalConfig
import com.sinch.verificationcore.config.method.VerificationMethodConfig
import java.util.concurrent.TimeUnit

/**
 * Configuration used by [Seaml] to handle flashcall verification.
 * @param globalConfig Global SDK configuration reference.
 * @param number Phone number that needs be verified.
 * @property honourEarlyReject Flag indicating if the verification process should honour early rejection rules.
 * @property custom Custom string that is passed with the initiation request.
 * @property maxTimeout Maximum timeout in milliseconds after which verification process reports the exception. Null if verification process should use only the timeout returned by the api.
 */
class SeamlessVerificationConfig internal constructor(
    globalConfig: GlobalConfig,
    number: String,
    honourEarlyReject: Boolean = true,
    custom: String? = null,
    maxTimeout: Long? = null
) : VerificationMethodConfig<SeamlessVerificationService>(
    globalConfig = globalConfig,
    number = number,
    honourEarlyReject = honourEarlyReject,
    custom = custom,
    apiService = globalConfig.retrofit.create(SeamlessVerificationService::class.java),
    maxTimeout = maxTimeout,
    acceptedLanguages = emptyList(),
    metadataFactory = AndroidMetadataFactory(
        globalConfig.context,
        BuildConfig.VERSION_NAME,
        BuildConfig.FLAVOR
    )
) {

    class Builder private constructor() :
        GlobalConfigSetter<SeamlessVerificationConfigCreator>,
        NumberSetter<SeamlessVerificationConfigCreator>,
        SeamlessVerificationConfigCreator {

        companion object {

            /**
             * Instance of builder that should be used to create [SeamlessVerificationConfig] object.
             */
            @JvmStatic
            val instance: GlobalConfigSetter<SeamlessVerificationConfigCreator>
                get() = Builder()
        }

        private lateinit var globalConfig: GlobalConfig
        private lateinit var number: String

        private var honourEarlyReject: Boolean = true
        private var custom: String? = null
        private var maxTimeout: Long? = null

        /**
         * Builds [SeamlessVerificationConfig] instance.
         * @return [SeamlessVerificationConfig] instance with previously defined parameters.
         */
        override fun build(): SeamlessVerificationConfig =
            SeamlessVerificationConfig(
                globalConfig = globalConfig,
                number = number,
                honourEarlyReject = honourEarlyReject,
                custom = custom,
                maxTimeout = maxTimeout
            )

        /**
         * Assigns honourEarlyReject flag to the builder.
         * @param honourEarlyReject Flag indicating if the verification process should honour early rejection rules.
         * @return Instance of builder with assigned flag.
         */
        override fun honourEarlyReject(honourEarlyReject: Boolean) = apply {
            this.honourEarlyReject = honourEarlyReject
        }

        /**
         * Assigns custom string to the builder.
         * @param custom Custom string that is passed with the initiation request.
         * @return Instance of builder with assigned custom field.
         */
        override fun custom(custom: String?) = apply {
            this.custom = custom
        }

        /**
         * Assigns maxTimeout value to the builder.
         * @param maxTimeout Maximum timeout after which verification process reports the exception. Null if verification process should use only the timeout returned by the api.
         * @param timeUnit Unit of [maxTimeout] parameter. Value is ignored if null is passed as [maxTimeout].
         * @return Instance of builder with assigned maxTimeout field.
         */
        override fun maxTimeout(maxTimeout: Long?, timeUnit: TimeUnit) = apply {
            this.maxTimeout = timeUnit.toMillisOrNull(maxTimeout)
        }

        /**
         * Assigns globalConfig value to the builder.
         * @param globalConfig Global SDK configuration reference.
         * @return Instance of builder with assigned globalConfig field.
         */
        override fun globalConfig(globalConfig: GlobalConfig): NumberSetter<SeamlessVerificationConfigCreator> =
            apply {
                this.globalConfig = globalConfig
            }

        /**
         * Assigns number value to the builder.
         * @param number Phone number that needs be verified.
         * @return Instance of builder with assigned number field.
         */
        override fun number(number: String): SeamlessVerificationConfigCreator = apply {
            this.number = number
        }

    }

}