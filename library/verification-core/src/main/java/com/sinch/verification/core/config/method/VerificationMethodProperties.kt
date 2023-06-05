package com.sinch.verification.core.config.method

import com.sinch.verification.core.verification.VerificationLanguage

/**
 * Base class for common properties of every verification method.
 * @property number Number that needs be verified.
 * @property custom Custom string that is passed with the initiation request.
 * @property reference Custom string that can be added for verification tracking purposes.
 * @property honourEarlyReject Flag indicating if the verification process should honour early rejection rules.
 * @property acceptedLanguages List of languages the verification process can use during the verification process.
 */
interface VerificationMethodProperties {
    val number: String?
    val custom: String?
    val reference: String?
    val honourEarlyReject: Boolean
    val acceptedLanguages: List<VerificationLanguage>
}