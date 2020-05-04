package com.sinch.verificationcore.config.method

import com.sinch.verificationcore.internal.Verification
import com.sinch.verificationcore.verification.response.VerificationListener

/**
 * Interface defining requirements of classes that can create [Verification] instances using builder pattern.
 * @param Listener Initiation Listener implementation
 */
interface VerificationMethodCreator<Listener> {

    /**
     * Assigns verification listener to the builder.
     * @param verificationListener Listener to be notified about the verification method results.
     * @return Instance of creator with assigned verification listener.
     */
    fun verificationListener(verificationListener: VerificationListener): VerificationMethodCreator<Listener>

    /**
     * Assigns initiation listener to the builder.
     * @param initializationListener Listener to be notified about the initiation method results.
     * @return Instance of creator with assigned initiation listener.
     */
    fun initializationListener(initializationListener: Listener): VerificationMethodCreator<Listener>

    /**
     * Builds verification instance.
     * @return Verification instance with previously defined parameters.
     */
    fun build(): Verification
}