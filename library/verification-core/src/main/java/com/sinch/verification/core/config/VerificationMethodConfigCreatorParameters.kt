package com.sinch.verification.core.config

import com.sinch.verification.core.verification.VerificationLanguage

interface VerificationMethodConfigCreatorParameters<Creator> {
    fun honourEarlyReject(honourEarlyReject: Boolean): Creator
    fun custom(custom: String?): Creator
    fun reference(reference: String?): Creator
    fun acceptedLanguages(acceptedLanguages: List<VerificationLanguage>): Creator
}