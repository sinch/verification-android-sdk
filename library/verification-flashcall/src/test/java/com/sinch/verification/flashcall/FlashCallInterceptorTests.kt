package com.sinch.verification.flashcall

import android.app.Application
import android.content.Context
import android.os.Build
import android.provider.CallLog
import android.telephony.TelephonyManager
import androidx.test.core.app.ApplicationProvider
import com.sinch.verification.flashcall.CallHistory.historyNum2
import com.sinch.verification.flashcall.CliTemplates.correctNumber1
import com.sinch.verification.flashcall.verification.callhistory.ContentProviderCallHistoryReader
import com.sinch.verification.flashcall.verification.interceptor.FlashCallInterceptor
import com.sinch.verification.flashcall.verification.matcher.FlashCallPatternMatcher
import com.sinch.verification.utils.MAX_TIMEOUT
import com.sinch.verification.core.internal.error.CodeInterceptionException
import com.sinch.verification.core.verification.VerificationSourceType
import com.sinch.verification.core.verification.interceptor.CodeInterceptionListener
import com.sinch.verification.core.verification.interceptor.CodeInterceptionTimeoutException
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import java.util.*
import java.util.concurrent.TimeUnit


@RunWith(
    RobolectricTestRunner::class
)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class FlashCallInterceptorTests {

    private val context = ApplicationProvider.getApplicationContext<Application>()
    private val telephonyManager =
        Shadows.shadowOf(context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager)
    private val contentResolverShadow =
        Shadows.shadowOf(context.contentResolver)

    @MockK(relaxed = true)
    lateinit var mockedInterceptionListener: CodeInterceptionListener

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun testListenerNotifiedAboutCorrectNumber() {
        createInterceptor().apply {
            start()
        }
        telephonyManager.setCallState(TelephonyManager.CALL_STATE_RINGING, correctNumber1)
        verify(exactly = 1) { mockedInterceptionListener.onCodeIntercepted(correctNumber1) }
        verify(exactly = 1) { mockedInterceptionListener.onCodeInterceptionStopped() }
    }

    @Test
    fun testListenerNotifiedAboutCorrectNumberFromLog() {
        val interceptor = createInterceptor()
        ContentProviderCallHistoryTests.insertMockedHistory(
            contentResolverShadow,
            System.currentTimeMillis()
        )
        interceptor.start()
        verify(exactly = 1) {
            mockedInterceptionListener.onCodeIntercepted(
                historyNum2,
                VerificationSourceType.LOG
            )
        }
        verify(exactly = 0) {
            mockedInterceptionListener.onCodeIntercepted(
                historyNum2,
                VerificationSourceType.INTERCEPTION
            )
        }
        assertTrue(contentResolverShadow.getContentObservers(CallLog.Calls.CONTENT_URI).isEmpty())
        verify(exactly = 1) { mockedInterceptionListener.onCodeInterceptionStopped() }
    }

    @Test
    fun testContentObserverRegisterAndUnregistered() {
        val interceptor = createInterceptor()
        interceptor.start()
        assertTrue(
            contentResolverShadow.getContentObservers(CallLog.Calls.CONTENT_URI).isNotEmpty()
        )
        interceptor.stop()
        assertTrue(contentResolverShadow.getContentObservers(CallLog.Calls.CONTENT_URI).isEmpty())
    }

    @Test
    fun testListenerNotNotifiedAboutWrongNumber() {
        createInterceptor().apply {
            start()
        }
        telephonyManager.setCallState(TelephonyManager.CALL_STATE_RINGING, "+48000")
        verify(exactly = 0) { mockedInterceptionListener.onCodeIntercepted(any()) }
        verify(exactly = 0) { mockedInterceptionListener.onCodeInterceptionStopped() }
    }

    @Test
    fun testListenerNotifiedWhenInitialStateNotIdle() {
        telephonyManager.setCallState(TelephonyManager.CALL_STATE_OFFHOOK)
        createInterceptor().apply {
            start()
        }
        telephonyManager.setCallState(TelephonyManager.CALL_STATE_RINGING, correctNumber1)
        verify(exactly = 1) { mockedInterceptionListener.onCodeIntercepted(correctNumber1) }
        verify(exactly = 1) { mockedInterceptionListener.onCodeInterceptionStopped() }
    }

    @Test
    fun testListenerNotifiedWhenAlreadyRinging() {
        telephonyManager.setCallState(TelephonyManager.CALL_STATE_RINGING, correctNumber1)
        createInterceptor().apply {
            start()
        }
        verify(exactly = 1) { mockedInterceptionListener.onCodeIntercepted(correctNumber1) }
        verify(exactly = 1) { mockedInterceptionListener.onCodeInterceptionStopped() }
    }

    @Test
    fun testListenerNotifiedAboutTimeout() {
        val exampleTimeout = 1000L
        createInterceptor(interceptionTimeout = exampleTimeout).apply {
            start()
        }
        Robolectric.getForegroundThreadScheduler()
            .advanceBy(exampleTimeout + 10, TimeUnit.MILLISECONDS)
        verify(exactly = 0) { mockedInterceptionListener.onCodeIntercepted(any()) }
        verify(exactly = 1) { mockedInterceptionListener.onCodeInterceptionError(any<CodeInterceptionTimeoutException>()) }
    }

    @Test
    fun testLateCallListenerNotifications() {
        val exampleTimeout = 1000L
        createInterceptor(
            interceptionTimeout = exampleTimeout,
            reportTimeout = exampleTimeout + 1000L
        ).apply {
            start()
        }
        Robolectric.getForegroundThreadScheduler()
            .advanceBy(exampleTimeout + 10, TimeUnit.MILLISECONDS)
        verify(exactly = 0) { mockedInterceptionListener.onCodeIntercepted(any()) }
        verify(exactly = 1) { mockedInterceptionListener.onCodeInterceptionError(any<CodeInterceptionTimeoutException>()) }
        verify(exactly = 0) { mockedInterceptionListener.onCodeInterceptionStopped() }
        Robolectric.getForegroundThreadScheduler()
            .advanceBy(exampleTimeout + 1000, TimeUnit.MILLISECONDS)
        verify(exactly = 1) { mockedInterceptionListener.onCodeInterceptionStopped() }
    }

    @Test
    fun testListenerNotNotifiedAboutCallsBetweenInterceptionAndReportTimeouts() {
        val exampleTimeout = 1000L
        createInterceptor(
            interceptionTimeout = exampleTimeout,
            reportTimeout = exampleTimeout * 2
        ).apply {
            start()
        }
        Robolectric.getForegroundThreadScheduler()
            .advanceBy(exampleTimeout + 10, TimeUnit.MILLISECONDS)
        telephonyManager.setCallState(TelephonyManager.CALL_STATE_RINGING, correctNumber1)
        verify(exactly = 0) { mockedInterceptionListener.onCodeIntercepted(any()) }
        Robolectric.getForegroundThreadScheduler()
            .advanceBy(exampleTimeout, TimeUnit.MILLISECONDS)
        verify(exactly = 1) { mockedInterceptionListener.onCodeInterceptionStopped() }
    }

    @Test(expected = CodeInterceptionException::class)
    fun testExceptionThrownWhenWrongReportTimeout() {
        val exampleTimeout = 1000L
        createInterceptor(interceptionTimeout = exampleTimeout, reportTimeout = exampleTimeout - 5)
    }

    @Test
    fun testNotificationsWhenReportAndInterceptionTimeoutSame() {
        createInterceptor()
        val exampleTimeout = 1000L
        createInterceptor(
            interceptionTimeout = exampleTimeout,
            reportTimeout = exampleTimeout
        ).apply {
            start()
        }
        Robolectric.getForegroundThreadScheduler()
            .advanceBy(exampleTimeout, TimeUnit.MILLISECONDS)
        verify(exactly = 0) { mockedInterceptionListener.onCodeIntercepted(any()) }
        verify(exactly = 1) { mockedInterceptionListener.onCodeInterceptionError(any<CodeInterceptionTimeoutException>()) }
        verify(exactly = 1) { mockedInterceptionListener.onCodeInterceptionStopped() }
    }

    @Test
    fun testListenerNotNotifiedWhenStopped() {
        createInterceptor().apply {
            start()
            stop()
        }
        telephonyManager.setCallState(TelephonyManager.CALL_STATE_RINGING, correctNumber1)
        verify(exactly = 0) { mockedInterceptionListener.onCodeIntercepted(correctNumber1) }
        verify(exactly = 1) { mockedInterceptionListener.onCodeInterceptionStopped() }
    }

    private fun createInterceptor(
        template: String = CliTemplates.template1,
        interceptionTimeout: Long = Long.MAX_TIMEOUT,
        reportTimeout: Long = interceptionTimeout + 15000,
        callHistoryStartDate: Date = Date(System.currentTimeMillis())
    ): FlashCallInterceptor = FlashCallInterceptor(
        context = context,
        flashCallPatternMatcher = FlashCallPatternMatcher(template),
        interceptionTimeout = interceptionTimeout,
        reportTimeout = reportTimeout,
        interceptionListener = mockedInterceptionListener,
        callHistoryReader = ContentProviderCallHistoryReader(context.contentResolver),
        callHistoryStartDate = callHistoryStartDate
    )
}