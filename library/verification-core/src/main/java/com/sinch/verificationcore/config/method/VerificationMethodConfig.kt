package com.sinch.verificationcore.config.method

import com.sinch.metadata.model.PhoneMetadataFactory
import com.sinch.verificationcore.config.general.GlobalConfig

abstract class VerificationMethodConfig<ApiService>(
    val globalConfig: GlobalConfig,
    val number: String,
    val custom: String = "",
    val apiService: ApiService,
    val honourEarlyReject: Boolean,
    val maxTimeout: Long?,
    val metadataFactory: PhoneMetadataFactory
)