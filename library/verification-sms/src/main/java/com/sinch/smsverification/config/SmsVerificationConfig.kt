package com.sinch.smsverification.config

import com.sinch.metadata.AndroidMetadataFactory
import com.sinch.smsverification.BuildConfig
import com.sinch.smsverification.SmsVerificationService
import com.sinch.verificationcore.config.GlobalConfigSetter
import com.sinch.verificationcore.config.NumberSetter
import com.sinch.verificationcore.config.general.GlobalConfig
import com.sinch.verificationcore.config.method.VerificationMethodConfig

class SmsVerificationConfig internal constructor(
    globalConfig: GlobalConfig,
    number: String,
    acceptedLanguages: List<String> = emptyList(),
    honourEarlyReject: Boolean = true,
    custom: String? = null,
    maxTimeout: Long? = null,
    val appHash: String? = null
) : VerificationMethodConfig<SmsVerificationService>(
    globalConfig = globalConfig,
    number = number,
    honourEarlyReject = honourEarlyReject,
    custom = custom,
    apiService = globalConfig.retrofit.create(SmsVerificationService::class.java),
    maxTimeout = maxTimeout,
    acceptedLanguages = acceptedLanguages,
    metadataFactory = AndroidMetadataFactory(
        globalConfig.context,
        BuildConfig.VERSION_NAME,
        BuildConfig.FLAVOR
    )
) {

    class Builder private constructor() : GlobalConfigSetter<SmsVerificationConfigCreator>,
        NumberSetter<SmsVerificationConfigCreator>, SmsVerificationConfigCreator {

        companion object {
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

        override fun honourEarlyReject(honourEarlyReject: Boolean) = apply {
            this.honourEarlyReject = honourEarlyReject
        }

        override fun custom(custom: String?) = apply {
            this.custom = custom
        }

        override fun maxTimeout(maxTimeout: Long?) = apply {
            this.maxTimeout = maxTimeout
        }

        override fun appHash(appHash: String?) = apply {
            this.appHash = appHash
        }

        override fun acceptedLanguages(languages: List<String>) = apply {
            this.acceptedLanguages = languages
        }

        override fun globalConfig(globalConfig: GlobalConfig): NumberSetter<SmsVerificationConfigCreator> =
            apply {
                this.globalConfig = globalConfig
            }

        override fun number(number: String): SmsVerificationConfigCreator = apply {
            this.number = number
        }

    }

}