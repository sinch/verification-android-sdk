package com.sinch.verificationcore.model

import com.sinch.verificationcore.BuildConfig

interface Config {
    val appKey: String
    val apiUrl: String get() = BuildConfig.API_URL
}