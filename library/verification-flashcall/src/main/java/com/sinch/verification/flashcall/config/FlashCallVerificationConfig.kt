package com.sinch.verification.flashcall.config

import com.sinch.metadata.AndroidMetadataFactory
import com.sinch.verification.flashcall.FlashCallVerificationService
import com.sinch.verificationcore.BuildConfig
import com.sinch.verificationcore.config.GlobalConfigSetter
import com.sinch.verificationcore.config.NumberSetter
import com.sinch.verificationcore.config.general.GlobalConfig
import com.sinch.verificationcore.config.method.VerificationMethodConfig

class FlashCallVerificationConfig internal constructor(
    globalConfig: GlobalConfig,
    number: String,
    acceptedLanguages: List<String> = emptyList(),
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
    acceptedLanguages = acceptedLanguages,
    metadataFactory = AndroidMetadataFactory(
        globalConfig.context,
        BuildConfig.VERSION_NAME,
        BuildConfig.FLAVOR
    )
) {

    class Builder private constructor() :
        GlobalConfigSetter<FlashCallVerificationConfigConfigCreator>,
        NumberSetter<FlashCallVerificationConfigConfigCreator>,
        FlashCallVerificationConfigConfigCreator {

        companion object {
            @JvmStatic
            val instance: GlobalConfigSetter<FlashCallVerificationConfigConfigCreator>
                get() = Builder()
        }

        private lateinit var globalConfig: GlobalConfig
        private lateinit var number: String

        private var honourEarlyReject: Boolean = true
        private var custom: String? = null
        private var maxTimeout: Long? = null
        private var appHash: String? = null
        private var acceptedLanguages: List<String> = emptyList()

        override fun build(): FlashCallVerificationConfig =
            FlashCallVerificationConfig(
                globalConfig = globalConfig,
                number = number,
                acceptedLanguages = acceptedLanguages,
                honourEarlyReject = honourEarlyReject,
                custom = custom,
                maxTimeout = maxTimeout
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

        override fun globalConfig(globalConfig: GlobalConfig): NumberSetter<FlashCallVerificationConfigConfigCreator> =
            apply {
                this.globalConfig = globalConfig
            }

        override fun number(number: String): FlashCallVerificationConfigConfigCreator = apply {
            this.number = number
        }

    }

}
