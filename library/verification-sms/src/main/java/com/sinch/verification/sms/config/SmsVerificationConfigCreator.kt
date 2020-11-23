package com.sinch.verification.sms.config

import com.sinch.verification.core.config.VerificationMethodConfigCreator

interface SmsVerificationConfigCreator :
    VerificationMethodConfigCreator<SmsVerificationConfigCreator, SmsVerificationConfig> {
    fun appHash(appHash: String?): SmsVerificationConfigCreator
}