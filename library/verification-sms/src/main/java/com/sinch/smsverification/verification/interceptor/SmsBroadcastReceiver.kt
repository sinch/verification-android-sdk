package com.sinch.smsverification.verification.interceptor

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status

class SmsBroadcastReceiver(private val listener: SmsBroadcastListener) : BroadcastReceiver() {

    fun registerOn(context: Context) =
        context.registerReceiver(this, IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION))

    override fun onReceive(context: Context?, intent: Intent?) {

        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent?.action) {
            val extras = intent.extras ?: Bundle.EMPTY
            val status: Status? = extras[SmsRetriever.EXTRA_STATUS] as? Status

            when (status?.statusCode) {
                CommonStatusCodes.SUCCESS ->
                    listener.onMessageReceived(extras[SmsRetriever.EXTRA_SMS_MESSAGE] as String)
                CommonStatusCodes.TIMEOUT ->
                    listener.onMessageFailedToReceive(SmsReceiverException("Exceeded GMS's retrieval window (t = 5 min)"))
                null -> listener.onMessageFailedToReceive(SmsReceiverException("Received bundle was malformed."))
            }

        }

    }

}