package com.sinch.verification.core.config

interface VerificationMethodConfigCreator<Creator, Config> :
    VerificationMethodConfigCreatorParameters<Creator> {
    fun build(): Config
}