package com.sinch.verificationcore.config.general

import com.sinch.verificationcore.auth.AuthorizationMethod
import okhttp3.Interceptor

interface ConfigBuilder {
    fun authMethod(authorizationMethod: AuthorizationMethod): ConfigBuilder
    fun apiHost(apiHost: String): ConfigBuilder
    fun interceptors(interceptors: List<Interceptor>): ConfigBuilder
    fun build(): GlobalConfig
}