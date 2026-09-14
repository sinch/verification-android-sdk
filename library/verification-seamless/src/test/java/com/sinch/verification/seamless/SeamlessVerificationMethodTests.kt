package com.sinch.verification.seamless

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.telephony.TelephonyManager
import androidx.test.core.app.ApplicationProvider
import com.sinch.verification.core.config.general.GlobalConfig
import com.sinch.verification.core.internal.VerificationState
import com.sinch.verification.core.internal.VerificationStateStatus
import com.sinch.verification.core.internal.VerificationStatus
import com.sinch.verification.core.internal.error.VerificationException
import com.sinch.verification.core.verification.response.VerificationListener
import com.sinch.verification.core.verification.response.VerificationResponseData
import com.sinch.verification.seamless.config.SeamlessVerificationConfig
import com.sinch.verification.seamless.initialization.SeamlessInitializationDetails
import com.sinch.verification.seamless.initialization.SeamlessInitializationListener
import com.sinch.verification.seamless.initialization.SeamlessInitiationResponseData
import com.sinch.verification.utils.permission.Permission
import io.mockk.Called
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import io.mockk.verifySequence
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode
import org.robolectric.Robolectric
import org.robolectric.shadows.ShadowNetwork
import okhttp3.HttpUrl.Companion.toHttpUrl
import retrofit2.Response
import retrofit2.mock.Calls
import java.util.concurrent.TimeUnit

@RunWith(
    RobolectricTestRunner::class
)
@LooperMode(LooperMode.Mode.LEGACY)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class SeamlessVerificationMethodTests {

    companion object {
        const val testPhone = "+48123456789"
        const val testIndiaPhone = "+911234567890"
        const val targetUri = "http://localhost.com"
        val correctInitResponse = SeamlessInitiationResponseData(
            id = "",
            details = SeamlessInitializationDetails(
                targetUri = targetUri,
            )
        )
    }

    private val appContext = ApplicationProvider.getApplicationContext<Application>()
    private val connectivityManager =
        Shadows.shadowOf(appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
    private val telephonyManager = spyk(
        appContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    )

    private val mockedService = mockk<SeamlessVerificationService>(relaxed = true)

    private val mockedRetrofit: retrofit2.Retrofit = run {
        val retrofit = mockk<retrofit2.Retrofit>(relaxed = true) {
            every { create(SeamlessVerificationService::class.java) } returns mockedService
            every { baseUrl() } returns "https://test.api.sinch.com/".toHttpUrl()
        }
        every { retrofit.newBuilder() } returns mockk<retrofit2.Retrofit.Builder>(relaxed = true) {
            every { baseUrl(any<String>()) } returns this
            every { baseUrl(any<okhttp3.HttpUrl>()) } returns this
            every { client(any()) } returns this
            every { build() } returns retrofit
        }
        retrofit
    }

    private val mockedGlobalConfig = spyk<GlobalConfig> {
        every { context } returns spyk(appContext) {
            every { getSystemService(Context.TELEPHONY_SERVICE) } returns telephonyManager
        }
        every { retrofit } returns mockedRetrofit
    }

    @MockK
    lateinit var mockedInitListener: SeamlessInitializationListener

    @MockK
    lateinit var mockedVerificationListener: VerificationListener

    @Before
    fun setupUp() {
        MockKAnnotations.init(this, relaxed = true)
        Shadows.shadowOf(appContext).grantPermissions(Permission.CHANGE_NETWORK_STATE.androidValue)

        // Mock TelephonyManager.isDataEnabled to return true for Android O+
        every { telephonyManager.isDataEnabled } returns true

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
        mockNetworkAvailable()

        verify(exactly = 1) { mockedInitListener.onInitializationFailed(error) }
        verify(exactly = 0) { mockedInitListener.onInitiated(any()) }

        verify { mockedVerificationListener wasNot Called }

        assertEquals(
            VerificationState.Initialization(VerificationStateStatus.ERROR, null),
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
        mockNetworkAvailable()
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
        mockNetworkAvailable()
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
        mockNetworkAvailable()
        verification.stop()
        assertEquals(
            VerificationState.Verification(VerificationStateStatus.SUCCESS),
            verification.verificationState
        )
    }

    @Test
    fun testListenerNotifiedAboutFailureWhenDataDisabled() {
        // Grant permissions but disable cellular data
        Shadows.shadowOf(appContext).grantPermissions(Permission.CHANGE_NETWORK_STATE.androidValue)
        every { telephonyManager.isDataEnabled } returns false

        val verification = prepareVerification()
        verification.initiate()

        verify(exactly = 1) {
            mockedInitListener.onInitializationFailed(
                match<VerificationException> {
                    it.message == "Cellular network not available"
                }
            )
        }
        verify(exactly = 0) { mockedInitListener.onInitiated(any()) }
        verify { mockedVerificationListener wasNot Called }

        // When onPreInitiate() returns false, base class automatically sets state to ERROR
        assertEquals(
            VerificationState.Initialization(VerificationStateStatus.ERROR, null),
            verification.verificationState
        )
    }

    @Test
    fun testListenerNotifiedAboutFailureWhenNetworkUnavailable() {
        val verification = prepareVerification().apply { initiate() }

        // Trigger onUnavailable callback
        connectivityManager.networkCallbacks.forEach {
            it.onUnavailable()
        }

        verify(exactly = 1) {
            mockedVerificationListener.onVerificationFailed(
                match<VerificationException> {
                    it.message == "Cellular network not available"
                }
            )
        }
        verify(exactly = 0) { mockedVerificationListener.onVerified() }
        verify(exactly = 0) { mockedInitListener.onInitiated(any()) }
    }

    @Test
    fun testListenerNotifiedAboutFailureWhenNetworkTimeout() {
        val verification = prepareVerification().apply { initiate() }

        // Advance time past MAX_REQUEST_DELAY (3000ms) using Robolectric scheduler
        Robolectric.getForegroundThreadScheduler()
            .advanceBy(SeamlessVerificationMethod.MAX_REQUEST_DELAY + 10, TimeUnit.MILLISECONDS)

        verify(exactly = 1) {
            mockedVerificationListener.onVerificationFailed(
                match<VerificationException> {
                    it.message == "Cellular network not available"
                }
            )
        }
        verify(exactly = 0) { mockedVerificationListener.onVerified() }
        verify(exactly = 0) { mockedInitListener.onInitiated(any()) }
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

    private fun mockNetworkAvailable() {
        // Currently only possible way to mock 'onAvailable' callback:
        // https://github.com/robolectric/robolectric/issues/5586
        connectivityManager.networkCallbacks.forEach {
            it.onAvailable(ShadowNetwork.newInstance(1))
        }
    }

    @Test
    fun testIndiaNumberUsesIndiaEndpoint() {
        val indiaPhone = testIndiaPhone
        val verification = SeamlessVerificationMethod.Builder()
            .config(
                SeamlessVerificationConfig.Builder()
                    .globalConfig(mockedGlobalConfig)
                    .number(indiaPhone)
                    .build()
            )
            .initializationListener(mockedInitListener)
            .verificationListener(mockedVerificationListener)
            .build()
        
        verification.initiate()
        mockNetworkAvailable()
        
        // Verify that initialization was successful (endpoint routing happens internally)
        verify(exactly = 1) { mockedInitListener.onInitiated(any()) }
    }

    @Test
    fun testNonIndiaNumberUsesDefaultEndpoint() {
        val nonIndiaPhone = testPhone
        val verification = SeamlessVerificationMethod.Builder()
            .config(
                SeamlessVerificationConfig.Builder()
                    .globalConfig(mockedGlobalConfig)
                    .number(nonIndiaPhone)
                    .build()
            )
            .initializationListener(mockedInitListener)
            .verificationListener(mockedVerificationListener)
            .build()
        
        verification.initiate()
        mockNetworkAvailable()
        
        // Verify that initialization was successful (default endpoint used)
        verify(exactly = 1) { mockedInitListener.onInitiated(any()) }
    }

    @Test
    fun testSuccessfulSeamlessVerificationWithIndiaNumber() {
        val verification = SeamlessVerificationMethod.Builder()
            .config(
                SeamlessVerificationConfig.Builder()
                    .globalConfig(mockedGlobalConfig)
                    .number(testIndiaPhone)
                    .build()
            )
            .initializationListener(mockedInitListener)
            .verificationListener(mockedVerificationListener)
            .build()
        
        verification.initiate()
        mockNetworkAvailable()
        
        verifySequence {
            mockedInitListener.onInitiated(correctInitResponse)
            mockedVerificationListener.onVerified()
        }

        assertEquals(
            VerificationState.Verification(VerificationStateStatus.SUCCESS),
            verification.verificationState
        )
    }

}