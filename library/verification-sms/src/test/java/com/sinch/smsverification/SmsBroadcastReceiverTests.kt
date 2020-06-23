package com.sinch.smsverification

import android.app.Application
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.test.core.app.ApplicationProvider
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import com.sinch.smsverification.verification.interceptor.SmsBroadcastListener
import com.sinch.smsverification.verification.interceptor.SmsBroadcastReceiver
import com.sinch.smsverification.verification.interceptor.SmsReceiverException
import io.mockk.MockKAnnotations
import io.mockk.called
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(
    RobolectricTestRunner::class
)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class SmsBroadcastReceiverTests {

    companion object {
        fun mockedBroadcastIntent(message: String, status: Status?): Intent =
            Intent(SmsRetriever.SMS_RETRIEVED_ACTION).apply {
                putExtras(Bundle().apply {
                    putString(SmsRetriever.EXTRA_SMS_MESSAGE, message)
                    putParcelable(SmsRetriever.EXTRA_STATUS, status)
                })
            }
    }

    private val context = ApplicationProvider.getApplicationContext<Application>()

    private val broadcastReceiver by lazy {
        SmsBroadcastReceiver(mockedListener)
    }

    @MockK(relaxed = true)
    lateinit var mockedListener: SmsBroadcastListener

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun testListenerNotifiedAboutTimeout() {
        val timeoutStatus = Status(CommonStatusCodes.TIMEOUT)
        broadcastReceiver.registerOn(context)
        context.sendBroadcast(mockedBroadcastIntent("", timeoutStatus))
        verify { mockedListener.onMessageFailedToReceive(any<SmsReceiverException>()) }
    }

    @Test
    fun testListenerNotifiedWhenMessageReceived() {
        val message = "Your code is 1234"
        broadcastReceiver.registerOn(context)
        context.sendBroadcast(mockedBroadcastIntent(message, Status(CommonStatusCodes.SUCCESS)))
        verify { mockedListener.onMessageReceived(message) }
    }

    @Test
    fun testBroadcastFailedWhenStatusIsNull() {
        val message = "Your code is 1234"
        broadcastReceiver.registerOn(context)
        context.sendBroadcast(mockedBroadcastIntent(message, null))
        verify { mockedListener.onMessageFailedToReceive(any<SmsReceiverException>()) }
    }

    @Test
    fun testBroadcastWithUnknownActionIgnored() {
        broadcastReceiver.registerOn(context)
        broadcastReceiver.onReceive(context, Intent())
        verify { mockedListener wasNot called }
    }
}