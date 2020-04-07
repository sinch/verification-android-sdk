package com.sinch.smsverification

import com.sinch.smsverification.config.SmsVerificationConfig
import com.sinch.smsverification.initialization.SmsInitializationListener
import com.sinch.verificationcore.config.method.VerificationMethodCreator

interface SmsVerificationConfigSetter {
    fun config(config: SmsVerificationConfig): VerificationMethodCreator<SmsInitializationListener>
}