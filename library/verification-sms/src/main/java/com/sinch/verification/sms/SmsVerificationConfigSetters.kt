package com.sinch.verification.sms

import com.sinch.verification.sms.config.SmsVerificationConfig
import com.sinch.verification.sms.initialization.SmsInitializationListener
import com.sinch.verification.core.config.method.VerificationMethodCreator

interface SmsVerificationConfigSetter {
    fun config(config: SmsVerificationConfig): VerificationMethodCreator<SmsInitializationListener>
}