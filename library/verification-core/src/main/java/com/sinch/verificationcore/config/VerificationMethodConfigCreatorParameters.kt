package com.sinch.verificationcore.config

import com.sinch.verificationcore.verification.VerificationLanguage

interface VerificationMethodConfigCreatorParameters<Creator> {
    fun honourEarlyReject(honourEarlyReject: Boolean): Creator
    fun custom(custom: String?): Creator
    fun reference(reference: String?): Creator
    fun acceptedLanguages(acceptedLanguages: List<VerificationLanguage>): Creator
}