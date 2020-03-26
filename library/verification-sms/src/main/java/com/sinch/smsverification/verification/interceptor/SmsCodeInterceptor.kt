package com.sinch.smsverification.verification.interceptor

import android.content.Context
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.tasks.OnFailureListener
import com.sinch.smsverification.verification.extractor.SmsCodeExtractor
import com.sinch.verificationcore.internal.error.CodeInterceptionException
import com.sinch.verificationcore.verification.interceptor.BasicCodeInterceptor
import com.sinch.verificationcore.verification.interceptor.CodeInterceptionListener
import timber.log.Timber

class SmsCodeInterceptor(
    private val context: Context,
    private val smsCodeExtractor: SmsCodeExtractor,
    maxTimeout: Long?,
    interceptionListener: CodeInterceptionListener
) : BasicCodeInterceptor(maxTimeout, interceptionListener), OnFailureListener,
    SmsBroadcastListener {

    private val smsRetrieverClient by lazy {
        SmsRetriever.getClient(context)
    }

    private val smsBroadcastReceiver by lazy {
        SmsBroadcastReceiver(this)
    }

    override fun onInterceptorStarted() {
        registerSmsBroadcastReceiver()
        smsRetrieverClient.startSmsRetriever().addOnFailureListener(this)
    }

    override fun onInterceptorStopped() {
        context.unregisterReceiver(smsBroadcastReceiver)
    }

    override fun onInterceptorTimedOut() {
    }

    override fun onFailure(e: Exception) {
        stop()
        interceptionListener.onCodeInterceptionError(
            CodeInterceptionException("Did not register for sms retrieval properly.")
        )
    }

    override fun onMessageReceived(message: String) {
        Timber.d("onMessageReceived $message")
        val extractedCode = smsCodeExtractor.extract(message)
        if (extractedCode != null) {
            interceptionListener.onCodeIntercepted(extractedCode)
        } else {
            interceptionListener.onCodeInterceptionError(CodeInterceptionException("Failed to extract code from message"))
        }
    }

    override fun onMessageFailedToReceive(e: Throwable) {
        interceptionListener.onCodeInterceptionError(e)
    }

    private fun registerSmsBroadcastReceiver() {
        smsBroadcastReceiver.registerOn(context)
    }

}