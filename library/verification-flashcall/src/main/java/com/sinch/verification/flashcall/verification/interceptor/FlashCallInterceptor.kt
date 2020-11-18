package com.sinch.verification.flashcall.verification.interceptor

import android.content.Context
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import com.sinch.verification.core.internal.error.CodeInterceptionException
import com.sinch.verification.core.internal.pattern.PatternMatcher
import com.sinch.verification.core.verification.VerificationSourceType
import com.sinch.verification.core.verification.interceptor.BasicCodeInterceptor
import com.sinch.verification.core.verification.interceptor.CodeInterceptionListener
import com.sinch.verification.core.verification.interceptor.CodeInterceptionTimeoutException
import com.sinch.verification.core.verification.response.VerificationListener
import com.sinch.verification.flashcall.FlashCallVerificationMethod
import com.sinch.verification.flashcall.FlashCallVerificationService
import com.sinch.verification.flashcall.initialization.FlashCallInitializationDetails
import com.sinch.verification.flashcall.verification.callhistory.CallHistoryChangeListener
import com.sinch.verification.flashcall.verification.callhistory.CallHistoryReader
import com.sinch.verification.flashcall.verification.callhistory.SinchCallHistoryChangeObserver
import java.util.*

/**
 * Code interceptor used to handle automatic verification code interception for
 * [FlashCallVerificationMethod] (by intercepting incoming phone calls).
 * @param context Context reference.
 * @param flashCallPatternMatcher Matcher to be used to check if incoming call matches [FlashCallInitializationDetails.cliFilter].
 * @param callHistoryReader Reader to be used to check call history for incoming sinch verification calls.
 * @param callHistoryStartDate Date from which interceptor should start checking call history for incoming verification call.
 * @param reportTimeout Timeout in milliseconds after which interceptor should stop and report it's work to the backend. (make [FlashCallVerificationService.reportVerification] request).
 * @param interceptionTimeout Maximum timeout in milliseconds after which [CodeInterceptionTimeoutException] is passed to the [VerificationListener]
 * @param interceptionListener Listener to be notified about the interception process results.
 */
class FlashCallInterceptor(
    private val context: Context,
    private val flashCallPatternMatcher: PatternMatcher,
    private val callHistoryReader: CallHistoryReader,
    private var callHistoryStartDate: Date,
    private val reportTimeout: Long,
    interceptionTimeout: Long,
    interceptionListener: CodeInterceptionListener
) :
    BasicCodeInterceptor(interceptionTimeout, interceptionListener), IncomingCallListener,
    CallHistoryChangeListener {

    init {
        if (interceptionTimeout > reportTimeout) {
            throw (CodeInterceptionException("Interception timeout cannot be greater then report timeout"))
        }
    }

    private val telephonyManager by lazy {
        context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    }

    private val flashCallStateListener by lazy {
        FlashCallStateListener(this)
    }

    private val callHistoryContentObserver by lazy {
        SinchCallHistoryChangeObserver(this)
    }

    private val delayedStopRunnable = Runnable {
        stop()
    }

    override val shouldInterceptorStopWhenTimedOut: Boolean
        get() = false

    var codeInterceptionState: CodeInterceptionState = CodeInterceptionState.NONE
        private set

    override fun onInterceptorStarted() {
        telephonyManager.listen(flashCallStateListener, PhoneStateListener.LISTEN_CALL_STATE)
        callHistoryContentObserver.registerOn(context.contentResolver)
        checkCallLog()
    }

    override fun onInterceptorStopped() {
        cancelHandler.removeCallbacks(delayedStopRunnable)
        telephonyManager.listen(flashCallStateListener, PhoneStateListener.LISTEN_NONE)
        callHistoryContentObserver.unregisterFrom(context.contentResolver)
    }

    override fun onInterceptorTimedOut() {
        val extraReportTimeout = reportTimeout - interceptionTimeout
        logger.debug("onInterceptorTimedOut, still reporting calls for $extraReportTimeout ms")
        cancelHandler.postDelayed(delayedStopRunnable, extraReportTimeout)
    }

    override fun onIncomingCallRinging(number: String) {
        if (flashCallPatternMatcher.matches(number)) {
            codeIntercepted(number, VerificationSourceType.INTERCEPTION)
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
            codeIntercepted(matchedNumber, VerificationSourceType.LOG)
        }
    }

    private fun codeIntercepted(number: String, sourceType: VerificationSourceType) {
        codeInterceptionState =
            if (isPastInterceptionTimeout) CodeInterceptionState.LATE else CodeInterceptionState.NORMAL
        if (!isPastInterceptionTimeout) {
            interceptionListener.onCodeIntercepted(number, sourceType)
        }
        stop()
    }

}