package com.sinch.smsverification.config

import com.sinch.metadata.AndroidMetadataFactory
import com.sinch.smsverification.BuildConfig
import com.sinch.smsverification.SmsVerificationMethod
import com.sinch.smsverification.SmsVerificationService
import com.sinch.utils.toMillisOrNull
import com.sinch.verificationcore.config.GlobalConfigSetter
import com.sinch.verificationcore.config.NumberSetter
import com.sinch.verificationcore.config.general.GlobalConfig
import com.sinch.verificationcore.config.method.VerificationMethodConfig
import java.util.concurrent.TimeUnit

/**
 * Configuration used by [SmsVerificationMethod] to handle sms verification.
 * @param globalConfig Global SDK configuration reference.
 * @param number Phone number that needs be verified.
 * @property acceptedLanguages List of languages the sms message with the verification code will be written in. Backend chooses the first one it can handle.
 * @property honourEarlyReject Flag indicating if the verification process should honour early rejection rules.
 * @property custom Custom string that is passed with the initiation request.
 * @property maxTimeout Maximum timeout in milliseconds after which verification process reports the exception. Null if verification process should use only the timeout returned by the api.
 * @property appHash Application hash used to automatically intercept the message. [See](https://developers.sinch.com/docs/verification-android-the-verification-process#automatic-code-extraction-from-sms)
 */
class SmsVerificationConfig internal constructor(
    globalConfig: GlobalConfig,
    number: String,
    acceptedLanguages: List<String> = emptyList(),
    honourEarlyReject: Boolean = true,
    custom: String? = null,
    maxTimeout: Long? = null,
    apiService: SmsVerificationService = globalConfig.retrofit.create(SmsVerificationService::class.java),
    val appHash: String? = null
) : VerificationMethodConfig<SmsVerificationService>(
    globalConfig = globalConfig,
    number = number,
    honourEarlyReject = honourEarlyReject,
    custom = custom,
    apiService = apiService,
    maxTimeout = maxTimeout,
    acceptedLanguages = acceptedLanguages,
    metadataFactory = AndroidMetadataFactory(
        globalConfig.context,
        BuildConfig.VERSION_NAME,
        BuildConfig.FLAVOR
    )
) {

    /**
     * Builder implementing fluent builder pattern to create [SmsVerificationConfig] objects.
     */
    class Builder private constructor() : GlobalConfigSetter<SmsVerificationConfigCreator>,
        NumberSetter<SmsVerificationConfigCreator>, SmsVerificationConfigCreator {

        companion object {

            /**
             * Instance of builder that should be used to create [SmsVerificationConfig] object.
             */
            @JvmStatic
            val instance: GlobalConfigSetter<SmsVerificationConfigCreator>
                get() = Builder()
        }

        private lateinit var globalConfig: GlobalConfig
        private lateinit var number: String

        private var honourEarlyReject: Boolean = true
        private var custom: String? = null
        private var maxTimeout: Long? = null
        private var appHash: String? = null
        private var acceptedLanguages: List<String> = emptyList()

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
                maxTimeout = maxTimeout,
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
         * Assigns maxTimeout value to the builder.
         * @param maxTimeout Maximum timeout after which verification process reports the exception. Null if verification process should use only the timeout returned by the api.
         * @param timeUnit Unit of [maxTimeout] parameter. Value is ignored if null is passed as [maxTimeout].
         * @return Instance of builder with assigned maxTimeout field.
         */
        override fun maxTimeout(maxTimeout: Long?, timeUnit: TimeUnit) = apply {
            this.maxTimeout = timeUnit.toMillisOrNull(maxTimeout)
        }

        /**
         * Assigns application hash value to the builder.
         * @param appHash Application hash used to automatically intercept the message. [See](https://developers.sinch.com/docs/verification-android-the-verification-process#automatic-code-extraction-from-sms)
         * @return Instance of builder with assigned appHash field.
         */
        override fun appHash(appHash: String?) = apply {
            this.appHash = appHash
        }

        /**
         * Assigns acceptedLanguages value to the builder.
         * @param acceptedLanguages List of languages the sms message with the verification code will be written in. Backend chooses the first one it can handle.
         * @return Instance of builder with assigned acceptedLanguages field.
         */
        override fun acceptedLanguages(acceptedLanguages: List<String>) = apply {
            this.acceptedLanguages = acceptedLanguages
        }

        /**
         * Assigns globalConfig value to the builder.
         * @param globalConfig Global SDK configuration reference.
         * @return Instance of builder with assigned globalConfig field.
         */
        override fun globalConfig(globalConfig: GlobalConfig): NumberSetter<SmsVerificationConfigCreator> =
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