package com.sinch.verification.all.auto.config

import com.sinch.metadata.AndroidMetadataFactory
import com.sinch.verification.all.auto.AutoVerificationService
import com.sinch.verification.core.BaseVerificationMethodConfigBuilder
import com.sinch.verification.core.config.GlobalConfigSetter
import com.sinch.verification.core.config.InitialSetter
import com.sinch.verification.core.config.general.GlobalConfig
import com.sinch.verification.core.config.method.VerificationMethodConfig
import com.sinch.verification.core.verification.VerificationLanguage
import com.sinch.verificationcore.BuildConfig

class AutoVerificationConfig internal constructor(
    globalConfig: GlobalConfig,
    number: String,
    honourEarlyReject: Boolean = true,
    custom: String? = null,
    reference: String? = null,
    val appHash: String? = null
) : VerificationMethodConfig<AutoVerificationService>(
    globalConfig = globalConfig,
    number = number,
    honourEarlyReject = honourEarlyReject,
    custom = custom,
    reference = reference,
    apiService = globalConfig.retrofit.create(AutoVerificationService::class.java),
    acceptedLanguages = emptyList(),
    metadataFactory = AndroidMetadataFactory(
        globalConfig.context,
        BuildConfig.SINCH_SDK_VERSION_NAME,
        BuildConfig.FLAVOR
    )
) {

    class Builder private constructor() :
        BaseVerificationMethodConfigBuilder<AutoVerificationConfigCreator>(),
        AutoVerificationConfigCreator {

        companion object {

            /**
             * Instance of builder that should be used to create [AutoVerificationConfig] object.
             */
            @JvmStatic
            val instance: GlobalConfigSetter<AutoVerificationConfigCreator>
                get() = Builder()

            operator fun invoke() = instance

        }

        private lateinit var globalConfig: GlobalConfig
        private var appHash: String? = null

        /**
         * Builds [AutoVerificationConfig] instance.
         * @return [AutoVerificationConfig] instance with previously defined parameters.
         */
        override fun build(): AutoVerificationConfig =
            AutoVerificationConfig(
                globalConfig = globalConfig,
                number = number.orEmpty(),
                honourEarlyReject = honourEarlyReject,
                custom = custom,
                reference = reference,
                appHash = appHash
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
        override fun globalConfig(globalConfig: GlobalConfig): InitialSetter<AutoVerificationConfigCreator> =
            apply {
                this.globalConfig = globalConfig
            }

        /**
         * Assigns number value to the builder.
         * @param number Phone number that needs be verified.
         * @return Instance of builder with assigned number field.
         */
        override fun number(number: String): AutoVerificationConfigCreator = apply {
            this.number = number
        }

        /**
         * Assigns application hash value to the builder.
         * @param appHash Application hash used to automatically intercept the message. [See](https://developers.sinch.com/docs/verification-android-the-verification-process#automatic-code-extraction-from-sms)
         * @return Instance of builder with assigned appHash field.
         */
        @Deprecated("For security purposes configure your application hash directly on the Sinch web portal.")
        override fun appHash(appHash: String?) = apply {
            this.appHash = appHash
        }

        override fun skipLocalInitialization(): AutoVerificationConfigCreator {
            throw UnsupportedOperationException(
                "Skipping local initialization is not yet supported for Auto " +
                    "verification"
            )
        }

        override fun acceptedLanguages(acceptedLanguages: List<VerificationLanguage>) =
            this.also {
                logger.debug("This verification method currently does not support accepted languages")
            }

    }

}