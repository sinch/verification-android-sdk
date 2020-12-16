package com.sinch.verification.core.verification.interceptor

import com.sinch.verification.core.internal.VerificationMethodType
import com.sinch.verification.core.verification.response.VerificationListener

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
     * Method that given interceptor is capable of intercepting codes from.
     */
    val method: VerificationMethodType

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