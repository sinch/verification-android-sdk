package com.sinch.verification.flashcall.verification.interceptor

import android.content.Context
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import com.sinch.verification.flashcall.verification.callhistory.CallHistoryChangeListener
import com.sinch.verification.flashcall.verification.callhistory.CallHistoryReader
import com.sinch.verification.flashcall.verification.callhistory.SinchCallHistoryChangeObserver
import com.sinch.verificationcore.internal.pattern.PatternMatcher
import com.sinch.verificationcore.verification.VerificationSourceType
import com.sinch.verificationcore.verification.interceptor.BasicCodeInterceptor
import com.sinch.verificationcore.verification.interceptor.CodeInterceptionListener
import java.util.*


class FlashCallInterceptor(
    private val context: Context,
    private val flashCallPatternMatcher: PatternMatcher,
    private val callHistoryReader: CallHistoryReader,
    private var callHistoryStartDate: Date,
    maxTimeout: Long?,
    interceptionListener: CodeInterceptionListener
) :
    BasicCodeInterceptor(maxTimeout, interceptionListener), IncomingCallListener,
    CallHistoryChangeListener {

    private val telephonyManager by lazy {
        context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    }

    private val flashCallStateListener by lazy {
        FlashCallStateListener(this)
    }

    private val callHistoryContentObserver by lazy {
        SinchCallHistoryChangeObserver(this)
    }

    override fun onInterceptorStarted() {
        telephonyManager.listen(flashCallStateListener, PhoneStateListener.LISTEN_CALL_STATE)
        callHistoryContentObserver.registerOn(context.contentResolver)
        checkCallLog()
    }

    override fun onInterceptorStopped() {
        telephonyManager.listen(flashCallStateListener, PhoneStateListener.LISTEN_NONE)
        callHistoryContentObserver.unregisterFrom(context.contentResolver)
    }

    override fun onInterceptorTimedOut() {}

    override fun onIncomingCallRinging(number: String) {
        if (flashCallPatternMatcher.matches(number)) {
            interceptionListener.onCodeIntercepted(number)
            stop()
        }
    }

    override fun onCallHistoryChanged() {
        checkCallLog()
    }

    private fun checkCallLog() {
        val dateInMs = callHistoryStartDate.time
        logger.debug("Checking call history since $callHistoryStartDate")
        callHistoryStartDate = Date()
        callHistoryReader.readIncomingCalls(dateInMs).firstOrNull { number ->
            flashCallPatternMatcher.matches(number)
        }?.let { matchedNumber ->
            interceptionListener.onCodeIntercepted(matchedNumber, VerificationSourceType.LOG)
            stop()
        }
    }


}