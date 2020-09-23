package com.sinch.verification.callout

import com.sinch.verification.callout.config.CalloutVerificationConfig
import com.sinch.verification.callout.initialization.CalloutInitializationListener
import com.sinch.verification.core.config.method.VerificationMethodCreator

interface CalloutVerificationConfigSetter {
    fun config(config: CalloutVerificationConfig): VerificationMethodCreator<CalloutInitializationListener>
}