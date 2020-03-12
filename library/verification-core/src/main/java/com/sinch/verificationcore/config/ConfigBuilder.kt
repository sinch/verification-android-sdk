package com.sinch.verificationcore.config

import android.content.Context
import com.sinch.verificationcore.auth.AuthMethod

interface ConfigBuilder {
    fun context(context: Context): ConfigBuilder
    fun authMethod(authMethod: AuthMethod): ConfigBuilder
    fun apiHost(apiHost: String): ConfigBuilder
    fun build(): Config
}