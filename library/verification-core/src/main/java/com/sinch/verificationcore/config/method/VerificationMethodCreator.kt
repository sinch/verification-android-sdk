package com.sinch.verificationcore.config.method

import com.sinch.verificationcore.internal.Verification
import com.sinch.verificationcore.verification.response.VerificationListener

interface VerificationMethodCreator<Listener> {
    fun verificationListener(verificationListener: VerificationListener): VerificationMethodCreator<Listener>
    fun initializationListener(initializationListener: Listener): VerificationMethodCreator<Listener>
    fun build(): Verification
}