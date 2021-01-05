package com.sinch.verification.all.auto;

import android.app.Application
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.sinch.verification.all.auto.initialization.AutoInitializationResponseData
import com.sinch.verification.all.auto.verification.interceptor.AutoVerificationInterceptor
import com.sinch.verification.all.auto.verification.interceptor.SubCodeInterceptionListener
import com.sinch.verification.all.auto.verification.interceptor.SubCodeInterceptorFactory
import com.sinch.verification.core.verification.interceptor.CodeInterceptionListener
import com.sinch.verification.core.verification.response.VerificationListener
import com.sinch.verification.flashcall.initialization.FlashCallInitializationDetails
import com.sinch.verification.sms.initialization.SmsInitializationDetails
import com.sinch.verification.utils.MAX_TIMEOUT
import io.mockk.Called
import io.mockk.MockKAnnotations
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
class AutoInterceptorTests {

    private val context = ApplicationProvider.getApplicationContext<Application>()

    @MockK(relaxed = true)
    lateinit var
            mockedInterceptionListener: CodeInterceptionListener

    @MockK(relaxed = true)
    lateinit var
            subCodeInterceptionListener: SubCodeInterceptionListener

    @MockK(relaxed = true)
    lateinit var
            mockedSubCodeInterceptorFactory: SubCodeInterceptorFactory

    @MockK(relaxed = true)
    lateinit var
            mockedVerificationListener: VerificationListener

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun testFlashcallDetailsInitializesSubInterceptor() {
        val data = AutoInitializationResponseData(
            id = "",
            flashcallDetails = FlashCallInitializationDetails(
                cliFilter = "",
                interceptionTimeoutApi = null,
                reportTimeoutApi = null,
                denyCallAfter = null
            )
        )
        val interceptor = createInterceptor(data)
        interceptor.start()
        verify { mockedSubCodeInterceptorFactory.create(data.flashcallDetails!!) }
    }

    @Test
    fun testSmsDetailsInitializesSubInterceptor() {
        val data = AutoInitializationResponseData(
            id = "",
            smsDetails = SmsInitializationDetails(
                template = "",
                interceptionTimeoutApi = Long.MAX_VALUE
            )
        )
        val interceptor = createInterceptor(data)
        interceptor.start()
        verify { mockedSubCodeInterceptorFactory.create(data.smsDetails!!) }
    }

    @Test
    fun testAutoInterceptedDetailsDoesNotCreateSubInterceptors() {
        val data = AutoInitializationResponseData(id = "")
        val interceptor = createInterceptor(data)
        interceptor.start()
        verify { mockedSubCodeInterceptorFactory wasNot Called }
    }

    private fun createInterceptor(
        data: AutoInitializationResponseData,
        interceptionTimeout: Long = Long.MAX_TIMEOUT
    ): AutoVerificationInterceptor =
        AutoVerificationInterceptor(
            context = context,
            autoCodeInterceptionListener = mockedInterceptionListener,
            subCodeInterceptionListener = subCodeInterceptionListener,
            interceptionTimeout = interceptionTimeout,
            autoInitializationResponseData = data,
            subInterceptorFactory = mockedSubCodeInterceptorFactory,
            verificationListener = mockedVerificationListener
        )

}