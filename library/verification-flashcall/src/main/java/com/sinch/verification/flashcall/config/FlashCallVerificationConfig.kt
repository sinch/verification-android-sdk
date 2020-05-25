package com.sinch.verification.flashcall.config

import com.sinch.metadata.AndroidMetadataFactory
import com.sinch.verification.flashcall.FlashCallVerificationMethod
import com.sinch.verification.flashcall.FlashCallVerificationService
import com.sinch.verificationcore.BaseVerificationMethodConfigBuilder
import com.sinch.verificationcore.BuildConfig
import com.sinch.verificationcore.config.GlobalConfigSetter
import com.sinch.verificationcore.config.NumberSetter
import com.sinch.verificationcore.config.general.GlobalConfig
import com.sinch.verificationcore.config.method.VerificationMethodConfig
import java.util.*

/**
 * Configuration used by [FlashCallVerificationMethod] to handle flashcall verification.
 * @param globalConfig Global SDK configuration reference.
 * @param number Phone number that needs be verified.
 * @property honourEarlyReject Flag indicating if the verification process should honour early rejection rules.
 * @property custom Custom string that is passed with the initiation request.
 */
class FlashCallVerificationConfig internal constructor(
    globalConfig: GlobalConfig,
    number: String,
    honourEarlyReject: Boolean = true,
    custom: String? = null,
    reference: String? = null
) : VerificationMethodConfig<FlashCallVerificationService>(
    globalConfig = globalConfig,
    number = number,
    honourEarlyReject = honourEarlyReject,
    custom = custom,
    reference = reference,
    apiService = globalConfig.retrofit.create(FlashCallVerificationService::class.java),
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
        BaseVerificationMethodConfigBuilder<FlashCallVerificationConfigCreator>(),
        FlashCallVerificationConfigCreator {

        companion object {

            /**
             * Instance of builder that should be used to create [FlashCallVerificationConfig] object.
             */
            @JvmStatic
            val instance: GlobalConfigSetter<FlashCallVerificationConfigCreator>
                get() = Builder()

            operator fun invoke() = instance

        }

        private lateinit var globalConfig: GlobalConfig

        /**
         * Builds [FlashCallVerificationConfig] instance.
         * @return [FlashCallVerificationConfig] instance with previously defined parameters.
         */
        override fun build(): FlashCallVerificationConfig =
            FlashCallVerificationConfig(
                globalConfig = globalConfig,
                number = number,
                honourEarlyReject = honourEarlyReject,
                custom = custom
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
         * Assigns reference string to the builder.
         * @param reference Reference string that is passed with the initiation request for tracking purposes.
         * @return Instance of builder with assigned reference field.
         */
        override fun reference(reference: String?) = apply {
            this.reference = reference
        }

        /**
         * Assigns globalConfig value to the builder.
         * @param globalConfig Global SDK configuration reference.
         * @return Instance of builder with assigned globalConfig field.
         */
        override fun globalConfig(globalConfig: GlobalConfig): NumberSetter<FlashCallVerificationConfigCreator> =
            apply {
                this.globalConfig = globalConfig
            }

        /**
         * Assigns number value to the builder.
         * @param number Phone number that needs be verified.
         * @return Instance of builder with assigned number field.
         */
        override fun number(number: String): FlashCallVerificationConfigCreator = apply {
            this.number = number
        }

        override fun acceptedLanguages(acceptedLanguages: List<Locale>) =
            this.also {
                logger.debug("This verification method currently does not support accepted languages")
            }

    }

}
