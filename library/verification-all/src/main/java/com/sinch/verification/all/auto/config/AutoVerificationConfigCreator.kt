package com.sinch.verification.all.auto.config

import com.sinch.verification.core.config.VerificationMethodConfigCreator

interface AutoVerificationConfigCreator :
    VerificationMethodConfigCreator<AutoVerificationConfigCreator, AutoVerificationConfig> {
    fun appHash(appHash: String?): AutoVerificationConfigCreator
}