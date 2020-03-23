package com.sinch.verificationcore.config.method

import com.sinch.metadata.model.PhoneMetadataFactory
import com.sinch.verificationcore.config.general.GeneralConfig

abstract class VerificationMethodConfig<ApiService>(
    val config: GeneralConfig,
    val number: String,
    val custom: String = "",
    val apiService: ApiService,
    val honourEarlyReject: Boolean,
    val metadataFactory: PhoneMetadataFactory
)