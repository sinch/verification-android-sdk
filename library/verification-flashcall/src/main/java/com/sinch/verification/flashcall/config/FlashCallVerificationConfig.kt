package com.sinch.verification.flashcall.config

import com.sinch.metadata.AndroidMetadataFactory
import com.sinch.verification.flashcall.FlashCallVerificationMethod
import com.sinch.verification.flashcall.FlashCallVerificationService
import com.sinch.verificationcore.BuildConfig
import com.sinch.verificationcore.config.GlobalConfigSetter
import com.sinch.verificationcore.config.NumberSetter
import com.sinch.verificationcore.config.general.GlobalConfig
import com.sinch.verificationcore.config.method.VerificationMethodConfig

/**
 * Configuration used by [FlashCallVerificationMethod] to handle flashcall verification.
 * @param globalConfig Global SDK configuration reference.
 * @param number Phone number that needs be verified.
 * @property honourEarlyReject Flag indicating if the verification process should honour early rejection rules.
 * @property custom Custom string that is passed with the initiation request.
 * @property maxTimeout Maximum timeout in milliseconds after which verification process reports the exception. Null if verification process should use only the timeout returned by the api.
 */
class FlashCallVerificationConfig internal constructor(
    globalConfig: GlobalConfig,
    number: String,
    honourEarlyReject: Boolean = true,
    custom: String? = null,
    maxTimeout: Long? = null
) : VerificationMethodConfig<FlashCallVerificationService>(
    globalConfig = globalConfig,
    number = number,
    honourEarlyReject = honourEarlyReject,
    custom = custom,
    apiService = globalConfig.retrofit.create(FlashCallVerificationService::class.java),
    maxTimeout = maxTimeout,
    acceptedLanguages = emptyList(),
    metadataFactory = AndroidMetadataFactory(
        globalConfig.context,
        BuildConfig.VERSION_NAME,
        BuildConfig.FLAVOR
    )
) {

    /**
     * Builder implementing fluent builder pattern to create [FlashCallVerificationConfig] objects.
     */
    class Builder private constructor() :
        GlobalConfigSetter<FlashCallVerificationConfigConfigCreator>,
        NumberSetter<FlashCallVerificationConfigConfigCreator>,
        FlashCallVerificationConfigConfigCreator {

        companion object {

            /**
             * Instance of builder that should be used to create [FlashCallVerificationConfig] object.
             */
            @JvmStatic
            val instance: GlobalConfigSetter<FlashCallVerificationConfigConfigCreator>
                get() = Builder()
        }

        private lateinit var globalConfig: GlobalConfig
        private lateinit var number: String

        private var honourEarlyReject: Boolean = true
        private var custom: String? = null
        private var maxTimeout: Long? = null

        /**
         * Builds [FlashCallVerificationConfig] instance.
         * @return [FlashCallVerificationConfig] instance with previously defined parameters.
         */
        override fun build(): FlashCallVerificationConfig =
            FlashCallVerificationConfig(
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
         * Assigns maxTimeout(ms) value to the builder.
         * @param maxTimeout Maximum timeout in milliseconds after which verification process reports the exception. Null if verification process should use only the timeout returned by the api.
         * @return Instance of builder with assigned maxTimeout field.
         */
        override fun maxTimeout(maxTimeout: Long?) = apply {
            this.maxTimeout = maxTimeout
        }

        /**
         * Assigns globalConfig value to the builder.
         * @param globalConfig Global SDK configuration reference.
         * @return Instance of builder with assigned globalConfig field.
         */
        override fun globalConfig(globalConfig: GlobalConfig): NumberSetter<FlashCallVerificationConfigConfigCreator> =
            apply {
                this.globalConfig = globalConfig
            }

        /**
         * Assigns number value to the builder.
         * @param number Phone number that needs be verified.
         * @return Instance of builder with assigned number field.
         */
        override fun number(number: String): FlashCallVerificationConfigConfigCreator = apply {
            this.number = number
        }

    }

}
