package com.sinch.verification.sms.verification.interceptor

import android.content.Context
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.tasks.OnFailureListener
import com.sinch.verification.core.internal.error.CodeInterceptionException
import com.sinch.verification.core.verification.CodeExtractorListener
import com.sinch.verification.core.verification.interceptor.BasicCodeInterceptor
import com.sinch.verification.core.verification.interceptor.CodeInterceptionListener
import com.sinch.verification.core.verification.interceptor.CodeInterceptionTimeoutException
import com.sinch.verification.core.verification.response.VerificationListener
import com.sinch.verification.sms.verification.interceptor.processor.MessageProcessor
import com.sinch.verification.sms.verification.interceptor.processor.SmsMessageProcessor
import kotlin.properties.Delegates

/**
 * Code interceptor used to handle automatic verification code interception from SMS messages.
 * @param context Context reference.
 * @param interceptionTimeout Maximum timeout in milliseconds after which [CodeInterceptionTimeoutException] is passed to the [VerificationListener]
 * @param interceptionListener Listener to be notified about the interception process results.
 */
class SmsCodeInterceptor(
    private val context: Context,
    interceptionTimeout: Long,
    interceptionListener: CodeInterceptionListener
) : BasicCodeInterceptor(interceptionTimeout, interceptionListener), OnFailureListener,
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

    @Suppress("RemoveExplicitTypeArguments")
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