package com.sinch.verificationcore.config.method

import com.sinch.verificationcore.internal.Verification
import com.sinch.verificationcore.verification.response.VerificationListener

interface VerificationMethodCreator<Listener> {
    fun verificationListener(verificationListener: VerificationListener): VerificationMethodCreator<Listener>
    fun initiationListener(initiationListener: Listener): VerificationMethodCreator<Listener>
    fun build(): Verification
}