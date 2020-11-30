package com.sinch.verification.all.auto.verification.interceptor

import android.content.Context
import com.sinch.verification.core.initiation.response.InitiationDetails
import com.sinch.verification.core.internal.VerificationMethodType
import com.sinch.verification.core.verification.interceptor.CodeInterceptor
import com.sinch.verification.flashcall.initialization.FlashCallInitializationDetails
import com.sinch.verification.flashcall.verification.callhistory.ContentProviderCallHistoryReader
import com.sinch.verification.flashcall.verification.interceptor.FlashCallInterceptor
import com.sinch.verification.flashcall.verification.matcher.FlashCallPatternMatcher
import com.sinch.verification.sms.initialization.SmsInitializationDetails
import com.sinch.verification.sms.verification.interceptor.SmsCodeInterceptor
import java.util.*

/**
 * [SubCodeInterceptorFactory] that creates CodeInterceptors that passes interception results
 * together with [VerificationMethodType] connected with the process.
 * @param context Android context reference
 * @param subCodeInterceptionListener Listener to be notified about the code interception results.
 */
class SinchSubCodeInterceptorFactory(
    private val context: Context,
    private val subCodeInterceptionListener: SubCodeInterceptionListener
) :
    SubCodeInterceptorFactory {

    override fun create(details: InitiationDetails): CodeInterceptor? {
        return details.run {
            when (this) {
                is SmsInitializationDetails -> SmsCodeInterceptor(
                    context = context,
                    interceptionListener = AutoVerificationMethodSubCodeInterceptionListener(
                        VerificationMethodType.SMS,
                        subCodeInterceptionListener
                    ),
                    interceptionTimeout = interceptionTimeout
                )
                is FlashCallInitializationDetails -> FlashCallInterceptor(
                    context = context,
                    interceptionTimeout = interceptionTimeout,
                    reportTimeout = reportTimeout,
                    interceptionListener = AutoVerificationMethodSubCodeInterceptionListener(
                        VerificationMethodType.FLASHCALL,
                        subCodeInterceptionListener
                    ),
                    flashCallPatternMatcher = FlashCallPatternMatcher(cliFilter),
                    callHistoryReader = ContentProviderCallHistoryReader(context.contentResolver),
                    callHistoryStartDate = Date() //TODO change to actual start of the initiation
                )
                else -> null
            }
        }
    }

}