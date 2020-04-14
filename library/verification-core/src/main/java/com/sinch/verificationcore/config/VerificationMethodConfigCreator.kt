package com.sinch.verificationcore.config

interface VerificationMethodConfigCreator<Creator, Config> {
    fun honourEarlyReject(honourEarlyReject: Boolean): Creator
    fun custom(custom: String?): Creator
    fun maxTimeout(maxTimeout: Long?): Creator
    fun build(): Config
}