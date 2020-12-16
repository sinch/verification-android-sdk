package com.sinch.verification.seamless

import com.sinch.verification.core.config.method.VerificationMethodCreator
import com.sinch.verification.seamless.config.SeamlessVerificationConfig
import com.sinch.verification.seamless.initialization.SeamlessInitializationListener

interface SeamlessVerificationConfigSetter {
    fun config(config: SeamlessVerificationConfig): VerificationMethodCreator<SeamlessInitializationListener>
}