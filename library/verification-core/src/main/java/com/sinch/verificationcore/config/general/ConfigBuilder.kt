package com.sinch.verificationcore.config.general

import android.content.Context
import com.sinch.verificationcore.auth.AuthorizationMethod
import okhttp3.Interceptor

interface ConfigBuilder {
    fun context(context: Context): ConfigBuilder
    fun authMethod(authorizationMethod: AuthorizationMethod): ConfigBuilder
    fun apiHost(apiHost: String): ConfigBuilder
    fun interceptors(interceptors: List<Interceptor>): ConfigBuilder
    fun build(): GlobalConfig
}