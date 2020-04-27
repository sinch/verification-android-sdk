package com.sinch.verificationcore.verification.interceptor

import com.sinch.verificationcore.verification.VerificationSourceType

interface CodeInterceptionListener {
    fun onCodeIntercepted(code: String, source: VerificationSourceType = VerificationSourceType.INTERCEPTION)
    fun onCodeInterceptionError(e: Throwable)
    fun onCodeInterceptionStopped()
}