package com.sinch.verification.sms.config

import com.sinch.verification.core.config.VerificationMethodConfigCreator

interface SmsVerificationConfigCreator :
    VerificationMethodConfigCreator<SmsVerificationConfigCreator, SmsVerificationConfig> {
    @Deprecated("For security purposes configure your application hash directly on the Sinch web portal.")
    fun appHash(appHash: String?): SmsVerificationConfigCreator
}