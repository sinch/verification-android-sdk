package com.sinch.verificationcore.config

import java.util.concurrent.TimeUnit

interface VerificationMethodConfigCreator<Creator, Config> {
    fun honourEarlyReject(honourEarlyReject: Boolean): Creator
    fun custom(custom: String?): Creator
    fun maxTimeout(maxTimeout: Long?, timeUnit: TimeUnit): Creator
    fun build(): Config
}