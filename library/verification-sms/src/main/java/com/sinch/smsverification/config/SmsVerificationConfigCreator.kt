package com.sinch.smsverification.config

import com.sinch.verificationcore.config.VerificationMethodConfigCreator

interface SmsVerificationConfigCreator :
    VerificationMethodConfigCreator<SmsVerificationConfigCreator, SmsVerificationConfig> {
    fun appHash(appHash: String?): SmsVerificationConfigCreator
    fun acceptedLanguages(acceptedLanguages: List<String>): SmsVerificationConfigCreator
}