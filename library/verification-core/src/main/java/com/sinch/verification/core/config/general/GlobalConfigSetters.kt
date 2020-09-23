package com.sinch.verification.core.config.general

import android.content.Context
import com.sinch.verification.core.auth.AuthorizationMethod

/**
 * Interface used by fluent builder pattern to pass the application context reference to global config instance.
 */
interface ApplicationContextSetter {

    /**
     * Assigns application context to the builder.
     * @param applicationContext Application context reference.
     * @return Instance of [AuthorizationMethodSetter]
     */
    fun applicationContext(applicationContext: Context): AuthorizationMethodSetter

}

/**
 * Interface used by fluent builder pattern to pass the authorizationMethod reference to global config instance.
 */
interface AuthorizationMethodSetter {

    /**
     * Assigns authorization method to the builder.
     * @param authorizationMethod Application context reference.
     * @return Instance of [GlobalConfigCreator]
     */
    fun authorizationMethod(authorizationMethod: AuthorizationMethod): GlobalConfigCreator

}