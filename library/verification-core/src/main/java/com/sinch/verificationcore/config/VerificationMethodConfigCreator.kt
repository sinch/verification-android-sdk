package com.sinch.verificationcore.config

interface VerificationMethodConfigCreator<Creator, Config> :
    VerificationMethodConfigCreatorParameters<Creator> {
    fun build(): Config
}