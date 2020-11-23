package com.sinch.verification.sms.verification.interceptor

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import com.sinch.logging.logger

/**
 * [BroadcastReceiver] that is responsible for extracting entire sms message that is suppose to contain
 * the verification code and pass it to the listener. More info can be found [here](https://developers.google.com/identity/sms-retriever).
 * @param listener Listener to be notified about the process result.
 */
class SmsBroadcastReceiver(private val listener: SmsBroadcastListener) : BroadcastReceiver() {

    private val logger = logger()

    fun registerOn(context: Context) =
        context.registerReceiver(this, IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION))

    override fun onReceive(context: Context?, intent: Intent?) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent?.action) {
            val extras = intent.extras ?: Bundle.EMPTY
            val status: Status? = extras[SmsRetriever.EXTRA_STATUS] as? Status
            logger.debug("onReceive called with status: $status message: ${extras[SmsRetriever.EXTRA_SMS_MESSAGE]}")
            when (status?.statusCode) {
                CommonStatusCodes.SUCCESS ->
                    listener.onMessageReceived(extras[SmsRetriever.EXTRA_SMS_MESSAGE] as String)
                CommonStatusCodes.TIMEOUT ->
                    listener.onMessageFailedToReceive(SmsReceiverException("Exceeded GMS's retrieval window (t = 5 min)"))
                null -> listener.onMessageFailedToReceive(SmsReceiverException("Received bundle was malformed."))
            }
        } else {
            logger.debug("onReceive called with unknown action: ${intent?.action}")
        }

    }

}