package com.sinch.smsverification.config

import com.sinch.metadata.AndroidMetadataFactory
import com.sinch.smsverification.BuildConfig
import com.sinch.smsverification.SmsVerificationService
import com.sinch.verificationcore.config.general.GlobalConfig
import com.sinch.verificationcore.config.method.VerificationMethodConfig

class SmsVerificationConfig @JvmOverloads constructor(
    config: GlobalConfig,
    number: String,
    acceptedLanguages: List<String> = emptyList(),
    honourEarlyReject: Boolean = true,
    custom: String? = null,
    maxTimeout: Long? = null,
    val appHash: String? = null
) : VerificationMethodConfig<SmsVerificationService>(
    globalConfig = config,
    number = number,
    honourEarlyReject = honourEarlyReject,
    custom = custom,
    apiService = config.retrofit.create(SmsVerificationService::class.java),
    maxTimeout = maxTimeout,
    acceptedLanguages = acceptedLanguages,
    metadataFactory = AndroidMetadataFactory(
        config.context,
        BuildConfig.VERSION_NAME,
        BuildConfig.FLAVOR
    )
) {

    class Builder(
        private val config: GlobalConfig,
        private val number: String
    ) {

        private var honourEarlyReject: Boolean = true
        private var custom: String? = null
        private var maxTimeout: Long? = null
        private var appHash: String? = null
        private var acceptedLanguages: List<String> = emptyList()

        fun build(): SmsVerificationConfig =
            SmsVerificationConfig(
                config = config,
                number = number,
                acceptedLanguages = acceptedLanguages,
                honourEarlyReject = honourEarlyReject,
                custom = custom,
                maxTimeout = maxTimeout,
                appHash = appHash
            )

        fun honourEarlyReject(honourEarlyReject: Boolean) = apply {
            this.honourEarlyReject = honourEarlyReject
        }

        fun custom(custom: String?) = apply {
            this.custom = custom
        }

        fun maxTimeout(maxTimeout: Long?) = apply {
            this.maxTimeout = maxTimeout
        }

        fun appHash(appHash: String?) = apply {
            this.appHash = appHash
        }

        fun acceptedLanguages(languages: List<String>) = apply {
            this.acceptedLanguages = languages
        }

    }

}