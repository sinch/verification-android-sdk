package com.sinch.verification.sms.config

import com.sinch.metadata.AndroidMetadataFactory
import com.sinch.verification.core.BaseVerificationMethodConfigBuilder
import com.sinch.verification.core.config.GlobalConfigSetter
import com.sinch.verification.core.config.InitialSetter
import com.sinch.verification.core.config.general.GlobalConfig
import com.sinch.verification.core.config.method.VerificationMethodConfig
import com.sinch.verification.core.verification.VerificationLanguage
import com.sinch.verification.sms.SmsVerificationMethod
import com.sinch.verification.sms.SmsVerificationService
import com.sinch.verificationcore.BuildConfig

/**
 * Configuration used by [SmsVerificationMethod] to handle sms verification.
 * @param globalConfig Global SDK configuration reference.
 * @param number Phone number that needs be verified.
 * @property acceptedLanguages List of languages the sms message with the verification code will be written in. Backend chooses the first one it can handle.
 * @property honourEarlyReject Flag indicating if the verification process should honour early rejection rules.
 * @property custom Custom string that is passed with the initiation request.
 * @property appHash Application hash used to automatically intercept the message. [See](https://developers.sinch.com/docs/verification-android-the-verification-process#automatic-code-extraction-from-sms)
 */
class SmsVerificationConfig internal constructor(
    globalConfig: GlobalConfig,
    number: String,
    acceptedLanguages: List<VerificationLanguage> = emptyList(),
    honourEarlyReject: Boolean = true,
    custom: String? = null,
    reference: String? = null,
    apiService: SmsVerificationService = globalConfig.retrofit.create(SmsVerificationService::class.java),
    val appHash: String? = null
) : VerificationMethodConfig<SmsVerificationService>(
    globalConfig = globalConfig,
    number = number,
    honourEarlyReject = honourEarlyReject,
    custom = custom,
    reference = reference,
    apiService = apiService,
    acceptedLanguages = acceptedLanguages,
    metadataFactory = AndroidMetadataFactory(
        globalConfig.context,
        BuildConfig.SINCH_SDK_VERSION_NAME,
        BuildConfig.FLAVOR
    )
) {

    /**
     * Builder implementing fluent builder pattern to create [SmsVerificationConfig] objects.
     */
    class Builder private constructor() :
        BaseVerificationMethodConfigBuilder<SmsVerificationConfigCreator>(),
        SmsVerificationConfigCreator {

        companion object {

            /**
             * Instance of builder that should be used to create [SmsVerificationConfig] object.
             */
            @JvmStatic
            val instance: GlobalConfigSetter<SmsVerificationConfigCreator>
                get() = Builder()

            operator fun invoke() = instance

        }

        private lateinit var globalConfig: GlobalConfig
        private var appHash: String? = null

        /**
         * Builds [SmsVerificationConfig] instance.
         * @return [SmsVerificationConfig] instance with previously defined parameters.
         */
        override fun build(): SmsVerificationConfig =
            SmsVerificationConfig(
                globalConfig = globalConfig,
                number = number,
                acceptedLanguages = acceptedLanguages,
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
         * Assigns application hash value to the builder.
         * @param appHash Application hash used to automatically intercept the message. [See](https://developers.sinch.com/docs/verification-android-the-verification-process#automatic-code-extraction-from-sms)
         * @return Instance of builder with assigned appHash field.
         */
        @Deprecated("For security purposes configure your application hash directly on the Sinch web portal.")
        override fun appHash(appHash: String?) = apply {
            this.appHash = appHash
        }

        /**
         * Assigns acceptedLanguages value to the builder.
         * @param acceptedLanguages List of languages the sms message with the verification code will be written in. Backend chooses the first one it can handle.
         * @return Instance of builder with assigned acceptedLanguages field.
         */
        override fun acceptedLanguages(acceptedLanguages: List<VerificationLanguage>) = apply {
            this.acceptedLanguages = acceptedLanguages
        }

        /**
         * Assigns globalConfig value to the builder.
         * @param globalConfig Global SDK configuration reference.
         * @return Instance of builder with assigned globalConfig field.
         */
        override fun globalConfig(globalConfig: GlobalConfig): InitialSetter<SmsVerificationConfigCreator> =
            apply {
                this.globalConfig = globalConfig
            }

        /**
         * Assigns number value to the builder.
         * @param number Phone number that needs be verified.
         * @return Instance of builder with assigned number field.
         */
        override fun number(number: String): SmsVerificationConfigCreator = apply {
            this.number = number
        }

    }

}