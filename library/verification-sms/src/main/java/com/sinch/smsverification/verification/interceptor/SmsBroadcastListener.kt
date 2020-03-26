package com.sinch.smsverification.verification.interceptor

interface SmsBroadcastListener {
    fun onMessageReceived(message: String)
    fun onMessageFailedToReceive(e: Throwable)
}