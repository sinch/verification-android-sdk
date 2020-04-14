package com.sinch.verification.flashcall.verification.interceptor

interface IncomingCallListener {
    fun onIncomingCallRinging(number: String)
}