package com.sinch.verification.seamless

import com.sinch.verification.seamless.config.SeamlessVerificationConfig
import com.sinch.verification.seamless.initialization.SeamlessInitializationListener
import com.sinch.verification.core.config.method.VerificationMethodCreator

interface SeamlessVerificationConfigSetter {
    fun config(config: SeamlessVerificationConfig): VerificationMethodCreator<SeamlessInitializationListener>
}