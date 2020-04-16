package com.sinch.smsverification.verification.interceptor

import android.content.Context
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.tasks.OnFailureListener
import com.sinch.smsverification.verification.interceptor.processor.MessageProcessor
import com.sinch.smsverification.verification.interceptor.processor.SmsMessageProcessor
import com.sinch.verificationcore.internal.error.CodeInterceptionException
import com.sinch.verificationcore.verification.CodeExtractorListener
import com.sinch.verificationcore.verification.interceptor.BasicCodeInterceptor
import com.sinch.verificationcore.verification.interceptor.CodeInterceptionListener
import kotlin.properties.Delegates

class SmsCodeInterceptor(
    private val context: Context,
    maxTimeout: Long?,
    interceptionListener: CodeInterceptionListener
) : BasicCodeInterceptor(maxTimeout, interceptionListener), OnFailureListener,
    SmsBroadcastListener, CodeExtractorListener {

    private val smsRetrieverClient by lazy {
        SmsRetriever.getClient(context)
    }

    private val smsBroadcastReceiver by lazy {
        SmsBroadcastReceiver(this)
    }

    private val smsProcessor: MessageProcessor by lazy {
        SmsMessageProcessor(this)
    }

    var smsTemplate: String? by Delegates.observable<String?>(null) { _, _, newTemplate ->
        newTemplate?.let { smsProcessor.onTemplateReceived(it) }
    }

    override fun onInterceptorStarted() {
        registerSmsBroadcastReceiver()
        smsRetrieverClient.startSmsRetriever().addOnFailureListener(this)
    }

    override fun onInterceptorStopped() {
        context.unregisterReceiver(smsBroadcastReceiver)
    }

    override fun onInterceptorTimedOut() {}

    override fun onFailure(e: Exception) {
        stop()
        interceptionListener.onCodeInterceptionError(
            CodeInterceptionException("Did not register for sms retrieval properly.")
        )
    }

    override fun onMessageReceived(message: String) {
        logger.debug("onMessageReceived $message")
        smsProcessor.onNewMessage(message)
    }

    override fun onMessageFailedToReceive(e: Throwable) {
        stop()
        interceptionListener.onCodeInterceptionError(e)
    }

    private fun registerSmsBroadcastReceiver() {
        smsBroadcastReceiver.registerOn(context)
    }

    override fun onCodeExtracted(code: String) {
        stop()
        interceptionListener.onCodeIntercepted(code)
    }

    override fun onCodeExtractionError(e: Throwable) {
        interceptionListener.onCodeInterceptionError(CodeInterceptionException("Failed to extract code from message"))
    }

}