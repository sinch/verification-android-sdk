package com.sinch.verificationcore.config.method

import com.sinch.metadata.model.PhoneMetadataFactory
import com.sinch.verificationcore.config.general.GlobalConfig

/**
 * Base class for common properties of every verification method.
 * @param ApiService Retrofit service class used for making API calls.
 * @property globalConfig Global SDK configuration reference.
 * @property number Number that needs be verified.
 * @property custom Custom string that is passed with the initiation request.
 * @property apiService Retrofit service reference used for making API calls.
 * @property honourEarlyReject Flag indicating if the verification process should honour early rejection rules.
 * @property maxTimeout Maximum timeout in milliseconds after which verification process reports the exception. Null if verification process should use only the timeout returned by the api.
 * @property acceptedLanguages List of languages the verification process can use during the verification process.
 * @property metadataFactory Factory to be used for collecting phone metadata.
 */
abstract class VerificationMethodConfig<ApiService>(
    val globalConfig: GlobalConfig,
    val number: String,
    val custom: String?,
    val apiService: ApiService,
    val honourEarlyReject: Boolean,
    val maxTimeout: Long?,
    val acceptedLanguages: List<String>,
    val metadataFactory: PhoneMetadataFactory
)