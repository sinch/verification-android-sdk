package com.sinch.smsverification

import android.app.Application
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.google.android.gms.common.api.Status
import com.sinch.smsverification.verification.interceptor.SmsBroadcastReceiver
import com.sinch.smsverification.verification.interceptor.SmsCodeInterceptor
import com.sinch.utils.MAX_TIMEOUT
import com.sinch.verificationcore.internal.error.CodeInterceptionException
import com.sinch.verificationcore.verification.interceptor.CodeInterceptionListener
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import java.util.concurrent.TimeUnit

@RunWith(
    RobolectricTestRunner::class
)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class SmsCodeInterceptorTests {

    companion object {
        const val CODE = "{{CODE}}"
        const val simpleTemplate = "Your code is $CODE"
        const val exampleCode = "1234"
    }

    private val context = ApplicationProvider.getApplicationContext<Application>()
    private val shadowApplication = Shadows.shadowOf(context)

    @MockK(relaxed = true)
    lateinit var mockedInterceptionListener: CodeInterceptionListener

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun testBroadcastReceiverRegisteredAfterStartCalled() {
        createInterceptor(simpleTemplate).apply { start() }
        Assert.assertTrue(shadowApplication.registeredReceivers.map { it.broadcastReceiver::class.simpleName }
            .contains(SmsBroadcastReceiver::class.simpleName))
    }

    @Test
    fun testBroadcastReceiverUnregisteredAfterStopCalled() {
        createInterceptor(simpleTemplate).apply {
            start()
            stop()
        }
        Assert.assertFalse(shadowApplication.registeredReceivers.map { it.broadcastReceiver::class.simpleName }
            .contains(SmsBroadcastReceiver::class.simpleName))
    }

    @Test
    fun testTimeoutExceptionThrown() {
        val timeout: Long = 1000
        createInterceptor(interceptionTimeout = timeout).apply { start() }
        Robolectric.getForegroundThreadScheduler().advanceBy(timeout / 2, TimeUnit.MILLISECONDS)
        verify(exactly = 0) { mockedInterceptionListener.onCodeInterceptionError(any<CodeInterceptionException>()) }
        Robolectric.getForegroundThreadScheduler().advanceBy(timeout, TimeUnit.MILLISECONDS)
        verify(exactly = 1) { mockedInterceptionListener.onCodeInterceptionError(any<CodeInterceptionException>()) }
        Assert.assertFalse(shadowApplication.registeredReceivers.map { it.broadcastReceiver::class.simpleName }
            .contains(SmsBroadcastReceiver::class.simpleName))
    }

    @Test
    fun testListenerNotifiedAboutSuccessfulInterception() {
        createInterceptor().apply {
            start()
            onMessageReceived(simpleTemplate.replace(CODE, exampleCode))
        }
        verify { mockedInterceptionListener.onCodeIntercepted(exampleCode) }
    }

    @Test
    fun testReceiverUnregisteredAfterSuccessfulInterception() {
        createInterceptor().apply {
            start()
            onMessageReceived(simpleTemplate.replace(CODE, exampleCode))
        }
        Assert.assertFalse(shadowApplication.registeredReceivers.map { it.broadcastReceiver::class.simpleName }
            .contains(SmsBroadcastReceiver::class.simpleName))
    }

    @Test
    fun testListenerNotifiedAboutInterceptionError() {
        val exampleException = CodeInterceptionException("Example message")
        createInterceptor().apply {
            start()
            onMessageFailedToReceive(exampleException)
        }
        verify { mockedInterceptionListener.onCodeInterceptionError(exampleException) }
    }

    @Test
    fun testListenerNotifiedAboutSuccessfulBroadcastInterception() {
        createInterceptor().apply {
            start()
        }
        context.sendBroadcast(
            SmsBroadcastReceiverTests.mockedBroadcastIntent(
                simpleTemplate.replace(CODE, exampleCode), Status.RESULT_SUCCESS
            )
        )
        verify { mockedInterceptionListener.onCodeIntercepted(exampleCode) }
    }

    @Test
    fun testListenerNotifiedWhenTimeoutStatusReached() {
        createInterceptor().apply {
            start()
        }
        context.sendBroadcast(
            SmsBroadcastReceiverTests.mockedBroadcastIntent(
                simpleTemplate.replace(CODE, exampleCode), Status.RESULT_TIMEOUT
            )
        )
        verify(exactly = 0) { mockedInterceptionListener.onCodeIntercepted(any()) }
        verify { mockedInterceptionListener.onCodeInterceptionError(any<CodeInterceptionException>()) }
    }

    @Test
    fun testListenerNotifiedWhenMalformedMessageReceived() {
        val malformedMessage = "1234 is your code"
        createInterceptor().apply {
            start()
        }
        context.sendBroadcast(
            SmsBroadcastReceiverTests.mockedBroadcastIntent(
                malformedMessage, Status.RESULT_SUCCESS
            )
        )
        verify(exactly = 0) { mockedInterceptionListener.onCodeIntercepted(any()) }
        verify { mockedInterceptionListener.onCodeInterceptionError(any<CodeInterceptionException>()) }
    }

    @Test
    fun testSmsMessageIgnoredAfterStopCalled() {
        createInterceptor().apply {
            start()
            stop()
        }
        context.sendBroadcast(
            SmsBroadcastReceiverTests.mockedBroadcastIntent(
                simpleTemplate.replace(CODE, exampleCode), Status.RESULT_TIMEOUT
            )
        )
        verify(exactly = 0) { mockedInterceptionListener.onCodeIntercepted(any()) }
        verify(exactly = 1) { mockedInterceptionListener.onCodeInterceptionStopped() }
    }

    private fun createInterceptor(
        template: String = simpleTemplate,
        interceptionTimeout: Long = Long.MAX_TIMEOUT
    ): SmsCodeInterceptor = SmsCodeInterceptor(
        context = context,
        interceptionTimeout = interceptionTimeout,
        interceptionListener = mockedInterceptionListener
    ).apply {
        smsTemplate = template
    }

}