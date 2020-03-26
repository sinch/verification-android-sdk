package com.sinch.smsverification.config

import com.sinch.metadata.AndroidMetadataFactory
import com.sinch.smsverification.BuildConfig
import com.sinch.smsverification.SmsVerificationService
import com.sinch.verificationcore.config.general.GlobalConfig
import com.sinch.verificationcore.config.method.VerificationMethodConfig

class SmsVerificationConfig(
    config: GlobalConfig,
    number: String,
    honourEarlyReject: Boolean = true,
    custom: String = "",
    maxTimeout: Long? = null,
    val appHash: String? = null
) : VerificationMethodConfig<SmsVerificationService>(
    globalConfig = config,
    number = number,
    honourEarlyReject = honourEarlyReject,
    custom = custom,
    apiService = config.retrofit.create(SmsVerificationService::class.java),
    maxTimeout = maxTimeout,
    metadataFactory = AndroidMetadataFactory(
        config.context,
        BuildConfig.VERSION_NAME,
        BuildConfig.FLAVOR
    )
)