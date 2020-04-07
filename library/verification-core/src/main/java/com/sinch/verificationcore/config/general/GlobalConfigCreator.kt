package com.sinch.verificationcore.config.general

import okhttp3.Interceptor

interface GlobalConfigCreator {
    fun apiHost(apiHost: String): GlobalConfigCreator
    fun interceptors(interceptors: List<Interceptor>): GlobalConfigCreator
    fun build(): GlobalConfig
}