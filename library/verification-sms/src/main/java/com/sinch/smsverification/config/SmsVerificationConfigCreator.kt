package com.sinch.smsverification.config

interface SmsVerificationConfigCreator {
    fun honourEarlyReject(honourEarlyReject: Boolean): SmsVerificationConfigCreator
    fun custom(custom: String?): SmsVerificationConfigCreator
    fun maxTimeout(maxTimeout: Long?): SmsVerificationConfigCreator
    fun appHash(appHash: String?): SmsVerificationConfigCreator
    fun acceptedLanguages(acceptedLanguages: List<String>): SmsVerificationConfigCreator
    fun build(): SmsVerificationConfig
}