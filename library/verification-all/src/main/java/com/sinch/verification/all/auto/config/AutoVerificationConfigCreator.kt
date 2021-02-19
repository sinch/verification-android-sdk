package com.sinch.verification.all.auto.config

import com.sinch.verification.core.config.VerificationMethodConfigCreator

interface AutoVerificationConfigCreator :
    VerificationMethodConfigCreator<AutoVerificationConfigCreator, AutoVerificationConfig> {
    @Deprecated("For security purposes configure your application hash directly on the Sinch web portal.")
    fun appHash(appHash: String?): AutoVerificationConfigCreator
}