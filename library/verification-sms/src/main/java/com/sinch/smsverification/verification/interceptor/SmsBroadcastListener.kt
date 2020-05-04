package com.sinch.smsverification.verification.interceptor

import com.google.android.gms.auth.api.phone.SmsRetriever

/**
 * Listener holding callbacks invoked whenever new [SmsRetriever.SMS_RETRIEVED_ACTION] is received
 * and processed by [SmsBroadcastReceiver].
 */
interface SmsBroadcastListener {

    /**
     * Callback invoked when [SmsBroadcastReceiver] has successfully extracted sms message.
     * @param message Received sms message.
     */
    fun onMessageReceived(message: String)

    /**
     * Callback invoked when [SmsBroadcastReceiver] reported an error.
     * @param e Error data.
     */
    fun onMessageFailedToReceive(e: Throwable)
}