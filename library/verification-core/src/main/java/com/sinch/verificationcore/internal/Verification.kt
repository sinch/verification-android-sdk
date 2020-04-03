package com.sinch.verificationcore.internal

interface Verification {

    val verificationState: VerificationState

    fun initiate()

    fun verify(verificationCode: String)

}