package com.sinch.verificationcore.internal

interface Verification {

    fun initiate()

    fun verify(verificationCode: String)

}