package com.sinch.verification.seamless

import android.app.Application
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.sinch.verification.seamless.config.SeamlessVerificationConfig
import com.sinch.verification.seamless.initialization.SeamlessInitializationDetails
import com.sinch.verification.seamless.initialization.SeamlessInitializationListener
import com.sinch.verification.seamless.initialization.SeamlessInitiationResponseData
import com.sinch.verification.utils.permission.Permission
import com.sinch.verification.core.config.general.GlobalConfig
import com.sinch.verification.core.internal.VerificationState
import com.sinch.verification.core.internal.VerificationStateStatus
import com.sinch.verification.core.internal.VerificationStatus
import com.sinch.verification.core.internal.error.VerificationException
import com.sinch.verification.core.verification.response.VerificationListener
import com.sinch.verification.core.verification.response.VerificationResponseData
import io.mockk.*
import io.mockk.impl.annotations.MockK
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import retrofit2.Response
import retrofit2.mock.Calls

@RunWith(
    RobolectricTestRunner::class
)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class SeamlessVerificationMethodTests {

    companion object {
        const val testPhone = "+48123456789"
        const val targetUri = "http://localhost.com"
        val correctInitResponse = SeamlessInitiationResponseData(
            id = "",
            details = SeamlessInitializationDetails(
                targetUri = targetUri
            )
        )
    }

    private val appContext = ApplicationProvider.getApplicationContext<Application>()

    private val mockedService = mockk<SeamlessVerificationService>(relaxed = true)

    private val mockedGlobalConfig = spyk<GlobalConfig> {
        every { context } returns (appContext)
        every { retrofit } returns mockk {
            every { create(SeamlessVerificationService::class.java) } returns mockedService
        }
    }

    @MockK
    lateinit var mockedInitListener: SeamlessInitializationListener

    @MockK
    lateinit var mockedVerificationListener: VerificationListener

    @Before
    fun setupUp() {
        MockKAnnotations.init(this, relaxed = true)
        Shadows.shadowOf(appContext).grantPermissions(Permission.CHANGE_NETWORK_STATE.androidValue)

        every { mockedService.initializeVerification(any()) }.returns(
            Calls.response(Response.success(correctInitResponse))
        )

        every { mockedService.verifySeamless(any()) } answers {
            if (firstArg<String>() == targetUri) {
                Calls.response(Response.success(mockk<VerificationResponseData>(relaxed = true).apply {
                    every { status } returns VerificationStatus.SUCCESSFUL
                }))
            } else {
                Calls.failure(Exception())
            }
        }
    }

    @Test
    fun testListenerNotifiedAboutFailureWhenPermissionsMissing() {
        Shadows.shadowOf(appContext).denyPermissions(Permission.CHANGE_NETWORK_STATE.androidValue)
        val verification = prepareVerification()
        verification.initiate()
        verify(exactly = 1) { mockedInitListener.onInitializationFailed(any<VerificationException>()) }
    }

    @Test
    fun testFailureInitializationNotifiesListener() {
        val error = mockk<Throwable>()
        every { mockedService.initializeVerification(any()) }.returns(
            Calls.failure(error)
        )
        val verification = prepareVerification().apply { initiate() }

        verify(exactly = 1) { mockedInitListener.onInitializationFailed(error) }
        verify(exactly = 0) { mockedInitListener.onInitiated(any()) }

        verify { mockedVerificationListener wasNot Called }

        assertEquals(
            VerificationState.Initialization(VerificationStateStatus.ERROR),
            verification.verificationState
        )
    }

    @Test
    fun testFailureVerificationNotifiesListener() {
        val error = mockk<Throwable>()
        every { mockedService.initializeVerification(any()) }.returns(
            Calls.response(
                correctInitResponse.copy(
                    details = SeamlessInitializationDetails("badUri")
                )
            )
        )
        val verification = prepareVerification().apply { initiate() }
        verify(exactly = 0) { mockedInitListener.onInitializationFailed(error) }
        verify(exactly = 1) { mockedInitListener.onInitiated(any()) }

        verify(exactly = 1) { mockedVerificationListener.onVerificationFailed(any<Exception>()) }
        verify(exactly = 0) { mockedVerificationListener.onVerified() }

        assertEquals(
            VerificationState.Verification(VerificationStateStatus.ERROR),
            verification.verificationState
        )
    }

    @Test
    fun testSuccessfulSeamlessVerificationListenerNotifications() {
        val verification = prepareVerification().apply { initiate() }

        verifySequence {
            mockedInitListener.onInitiated(correctInitResponse)
            mockedVerificationListener.onVerified()
        }

        assertEquals(
            VerificationState.Verification(VerificationStateStatus.SUCCESS),
            verification.verificationState
        )
    }

    @Test
    fun testManuallyStoppingFinishedVerificationKeepsStatus() {
        val verification = prepareVerification().apply { initiate() }
        verification.stop()
        assertEquals(
            VerificationState.Verification(VerificationStateStatus.SUCCESS),
            verification.verificationState
        )
    }

    private fun prepareVerification() =
        SeamlessVerificationMethod.Builder()
            .config(
                SeamlessVerificationConfig.Builder()
                    .globalConfig(mockedGlobalConfig)
                    .number(testPhone)
                    .build()
            )
            .initializationListener(mockedInitListener)
            .verificationListener(mockedVerificationListener)
            .build()


}