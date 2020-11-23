package com.sinch.verificationcore.query

import android.app.Application
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.sinch.verification.core.config.general.GlobalConfig
import com.sinch.verification.core.internal.VerificationMethodType
import com.sinch.verification.core.query.SinchVerificationQuery
import com.sinch.verification.core.query.VerificationQueryService
import com.sinch.verification.core.internal.VerificationStatus
import com.sinch.verification.core.query.callback.VerificationInfoCallback
import com.sinch.verification.core.verification.response.VerificationResponseData
import io.mockk.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import retrofit2.mock.Calls

@RunWith(
    RobolectricTestRunner::class
)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class SinchVerificationQueryTests {

    private val appContext = ApplicationProvider.getApplicationContext<Application>()

    private val mockedService = mockk<VerificationQueryService>(relaxed = true)

    private val mockedGlobalConfig = spyk<GlobalConfig> {
        every { context } returns (appContext)
        every { retrofit } returns mockk {
            every { create(com.sinch.verification.core.query.VerificationQueryService::class.java) } returns mockedService
        }
    }

    private val verificationQuery by lazy {
        SinchVerificationQuery.withGlobalConfig(mockedGlobalConfig)
    }

    @Before
    fun setupUp() {
        MockKAnnotations.init(this, relaxed = true)
    }

    @Test
    fun testSuccessfulIdQuery() {
        val myId = "testId"
        val myCallback: VerificationInfoCallback = mockk(relaxed = true)
        mockSuccessfulCalls()
        verificationQuery.queryById(myId, myCallback)
        verify { myCallback.onSuccess(match { it.id == myId }) }
        verify(exactly = 0) { myCallback.onError(any()) }
    }

    @Test
    fun testSuccessfulReferenceQuery() {
        val myReference = "testReference"
        val myCallback: VerificationInfoCallback = mockk(relaxed = true)
        mockSuccessfulCalls()
        verificationQuery.queryByReference(myReference, myCallback)
        verify { myCallback.onSuccess(match { it.reference == myReference }) }
        verify(exactly = 0) { myCallback.onError(any()) }
    }

    @Test
    fun testSuccessfulEndpointQuery() {
        val myMethod = VerificationMethodType.SMS
        val myCallback: VerificationInfoCallback = mockk(relaxed = true)
        val myNumber = "123"
        mockSuccessfulCalls()
        verificationQuery.queryByEndpoint(myMethod, myNumber, myCallback)
        verify { myCallback.onSuccess(match { it.method == myMethod }) }
        verify(exactly = 0) { myCallback.onError(any()) }
    }

    @Test
    fun testErrorIdQuery() {
        val myCallback: VerificationInfoCallback = mockk(relaxed = true)
        mockErrorCalls()
        verificationQuery.queryById("", myCallback)
        verify(exactly = 0) { myCallback.onSuccess(any()) }
        verify(exactly = 1) { myCallback.onError(any()) }
    }

    @Test
    fun testErrorReferenceQuery() {
        val myCallback: VerificationInfoCallback = mockk(relaxed = true)
        mockErrorCalls()
        verificationQuery.queryByReference("", myCallback)
        verify(exactly = 0) { myCallback.onSuccess(any()) }
        verify(exactly = 1) { myCallback.onError(any()) }
    }

    @Test
    fun testErrorEndpointQuery() {
        val myCallback: VerificationInfoCallback = mockk(relaxed = true)
        mockErrorCalls()
        verificationQuery.queryByEndpoint(VerificationMethodType.SMS, "", myCallback)
        verify(exactly = 0) { myCallback.onSuccess(any()) }
        verify(exactly = 1) { myCallback.onError(any()) }
    }

    private fun mockSuccessfulCalls() {
        every { mockedService.queryByReference(any()) } answers {
            Calls.response(
                VerificationResponseData(
                    id = "",
                    status = VerificationStatus.SUCCESSFUL,
                    reference = firstArg(),
                    method = VerificationMethodType.FLASHCALL
                )
            )
        }

        every {
            mockedService.queryByEndpoint(
                any(),
                any()
            )
        } answers {
            Calls.response(
                VerificationResponseData(
                    id = "",
                    status = VerificationStatus.SUCCESSFUL,
                    method = firstArg()
                )
            )
        }

        every { mockedService.queryById(any()) } answers {
            Calls.response(
                VerificationResponseData(
                    id = firstArg(),
                    status = VerificationStatus.SUCCESSFUL,
                    method = VerificationMethodType.FLASHCALL
                )
            )
        }

    }

    private fun mockErrorCalls() {
        every { mockedService.queryById(any()) } returns Calls.failure(Exception())
        every { mockedService.queryByEndpoint(any(), any()) } returns Calls.failure(Exception())
        every { mockedService.queryByReference(any()) } returns Calls.failure(Exception())
    }

}