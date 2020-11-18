package com.sinch.verification.all.auto

import com.sinch.verification.all.auto.config.AutoVerificationConfig
import com.sinch.verification.all.auto.initialization.AutoInitializationListener
import com.sinch.verification.core.config.method.VerificationMethodCreator

interface AutoVerificationConfigSetter {
    fun config(config: AutoVerificationConfig): VerificationMethodCreator<AutoInitializationListener>
}