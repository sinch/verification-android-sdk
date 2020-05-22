package com.sinch.verificationcore.config

interface VerificationMethodConfigCreatorParameters<Creator> {
    fun honourEarlyReject(honourEarlyReject: Boolean): Creator
    fun custom(custom: String?): Creator
    fun reference(reference: String?): Creator
    fun acceptedLanguages(acceptedLanguages: List<String>): Creator
}