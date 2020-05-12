package com.sinch.verificationcore.config.method

/**
 * Base class for common properties of every verification method.
 * @property number Number that needs be verified.
 * @property custom Custom string that is passed with the initiation request.
 * @property honourEarlyReject Flag indicating if the verification process should honour early rejection rules.
 * @property maxTimeout Maximum timeout in milliseconds after which verification process reports the exception. Null if verification process should use only the timeout returned by the api.
 * @property acceptedLanguages List of languages the verification process can use during the verification process.
 */
interface VerificationMethodProperties {
    val number: String
    val custom: String?
    val honourEarlyReject: Boolean
    val maxTimeout: Long?
    val acceptedLanguages: List<String>
}