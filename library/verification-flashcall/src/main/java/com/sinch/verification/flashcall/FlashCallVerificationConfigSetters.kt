package com.sinch.verification.flashcall

import com.sinch.verification.flashcall.config.FlashCallVerificationConfig
import com.sinch.verification.flashcall.initialization.FlashCallInitializationListener
import com.sinch.verificationcore.config.method.VerificationMethodCreator

interface FlashCallVerificationConfigSetter {
    fun config(config: FlashCallVerificationConfig): VerificationMethodCreator<FlashCallInitializationListener>
}