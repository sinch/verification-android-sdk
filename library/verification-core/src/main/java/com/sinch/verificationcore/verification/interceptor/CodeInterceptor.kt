package com.sinch.verificationcore.verification.interceptor

import com.sinch.verificationcore.verification.response.VerificationListener

/**
 * The interceptor's goal is to automatically grab the verification code from the source defined by each verification method.
 * This interface holds common requirements each implementation must follow.
 */
interface CodeInterceptor {

    /**
     * Maximum timeout in milliseconds after which [CodeInterceptionTimeoutException] is passed to the [VerificationListener]
     */
    var interceptionTimeout: Long

    /**
     * Listener to be notified about the interception process results.
     */
    val interceptionListener: CodeInterceptionListener

    /**
     * Current state of the interception process.
     */
    val state: InterceptorState

    /**
     * Starts the interceptor.
     */
    fun start()

    /**
     * Stops the interceptor.
     */
    fun stop()
}