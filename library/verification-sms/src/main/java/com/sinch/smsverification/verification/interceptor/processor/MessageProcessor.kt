package com.sinch.smsverification.verification.interceptor.processor

interface MessageProcessor {
    fun onNewMessage(message: String)
    fun onTemplateReceived(template: String)
}