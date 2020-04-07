package com.sinch.verificationcore.config.general

import android.content.Context
import com.sinch.verificationcore.auth.AuthorizationMethod

interface ApplicationContextSetter {
    fun applicationContext(applicationContext: Context): AuthorizationMethodSetter
}

interface AuthorizationMethodSetter {
    fun authorizationMethod(authorizationMethod: AuthorizationMethod): GlobalConfigCreator
}