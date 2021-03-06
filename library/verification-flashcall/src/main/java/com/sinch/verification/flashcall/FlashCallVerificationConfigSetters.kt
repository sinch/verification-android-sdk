package com.sinch.verification.flashcall

import com.sinch.verification.core.config.method.VerificationMethodCreator
import com.sinch.verification.flashcall.config.FlashCallVerificationConfig
import com.sinch.verification.flashcall.initialization.FlashCallInitializationListener

interface FlashCallVerificationConfigSetter {
    fun config(config: FlashCallVerificationConfig): VerificationMethodCreator<FlashCallInitializationListener>
}