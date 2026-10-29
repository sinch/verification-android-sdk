@file:OptIn(ExperimentalDigitalCredentialApi::class, ExperimentalCoroutinesApi::class)

package com.sinch.verification.seamless

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build
import androidx.credentials.CredentialManager
import androidx.credentials.DigitalCredential
import androidx.credentials.ExperimentalDigitalCredentialApi
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.test.core.app.ApplicationProvider
import com.sinch.verification.core.config.general.GlobalConfig
import com.sinch.verification.core.internal.VerificationState
import com.sinch.verification.core.internal.VerificationStateStatus
import com.sinch.verification.core.internal.VerificationStatus
import com.sinch.verification.core.internal.error.VerificationException
import com.sinch.verification.core.verification.response.VerificationListener
import com.sinch.verification.core.verification.response.VerificationResponseData
import com.sinch.verification.seamless.config.SeamlessVerificationConfig
import com.sinch.verification.seamless.digitalcredential.AdunaV2TestFixtures
import com.sinch.verification.seamless.initialization.SeamlessInitializationDetails
import com.sinch.verification.seamless.initialization.SeamlessInitializationListener
import com.sinch.verification.seamless.initialization.SeamlessInitiationResponseData
import com.sinch.verification.utils.permission.Permission
import io.mockk.Called
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.slot
import io.mockk.spyk
import io.mockk.unmockkObject
import io.mockk.verify
import io.mockk.verifySequence
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.serialization.json.Json
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode
import org.robolectric.shadows.ShadowNetwork
import retrofit2.Response
import retrofit2.mock.Calls

@RunWith(RobolectricTestRunner::class)
@LooperMode(LooperMode.Mode.LEGACY)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class SeamlessVerificationMethodTests {

    companion object {
        const val testPhone = "+48123456789"
        const val targetUri = "http://localhost.com"
        const val v2VerificationId = "v2-test-id"
        const val testCredentialJson = """{"data":{"vp_token":{"aduna":["test-vp-token-abc123"]}}}"""
        const val carrierAppUrl = "https://carrier.example.com/verify"
        const val appInfoJwt = "test-app-info-jwt"
        const val appInfoJwtQueryParameterName = "app_info"
        const val appCallbackQueryParameterName = "operator_token"
        const val callbackToken = "test-operator-token"

        val correctInitResponse = SeamlessInitiationResponseData(
            id = "",
            details = SeamlessInitializationDetails(targetUri = targetUri)
        )

        val v2InitResponseWithVp: SeamlessInitiationResponseData =
            Json { ignoreUnknownKeys = true }.decodeFromString(
                """{"id":"$v2VerificationId","method":"seamless","seamless":{"version":"v2","nonce":"${AdunaV2TestFixtures.NONCE}","vpResponse":${AdunaV2TestFixtures.VP_RESPONSE_JSON}}}"""
            )

        // v2 response without vpResponse
        val v2InitResponseNoVp = SeamlessInitiationResponseData(
            id = v2VerificationId,
            details = SeamlessInitializationDetails(version = "v2")
        )

        val v2InitResponseWithAppUrl: SeamlessInitiationResponseData =
            Json { ignoreUnknownKeys = true }.decodeFromString(
                """{"id":"$v2VerificationId","method":"seamless","seamless":{"version":"v2","androidAppUrl":"$carrierAppUrl","appInfoJwt":"$appInfoJwt","appInfoJwtQueryParameterName":"$appInfoJwtQueryParameterName","appCallbackQueryParameterName":"$appCallbackQueryParameterName"}}"""
            )
    }

    private val testDispatcher = UnconfinedTestDispatcher()

    private val appContext = ApplicationProvider.getApplicationContext<Application>()
    private val connectivityManager =
        Shadows.shadowOf(appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)

    private val mockedService = mockk<SeamlessVerificationService>(relaxed = true)
    private val mockedCredentialManager = mockk<CredentialManager>(relaxed = true)
    private val mockedActivity = mockk<Activity>(relaxed = true)

    private val mockedGlobalConfig = spyk<GlobalConfig> {
        every { context } returns appContext
        every { json } returns Json { encodeDefaults = true; ignoreUnknownKeys = true }
        every { retrofit } returns mockk {
            every { create(SeamlessVerificationService::class.java) } returns mockedService
        }
    }

    @MockK
    lateinit var mockedInitListener: SeamlessInitializationListener

    @MockK
    lateinit var mockedVerificationListener: VerificationListener

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxed = true)
        Dispatchers.setMain(testDispatcher)

        mockkObject(CredentialManager.Companion)
        every { CredentialManager.create(any<Context>()) } returns mockedCredentialManager

        Shadows.shadowOf(appContext).grantPermissions(Permission.CHANGE_NETWORK_STATE.androidValue)

        every { mockedService.initializeVerification(any()) } returns
            Calls.response(Response.success(correctInitResponse))

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

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkObject(CredentialManager.Companion)
    }

    // ---- v1 (cellular redirect) tests ----

    @Test
    fun testListenerNotifiedAboutFailureWhenPermissionsMissing() {
        Shadows.shadowOf(appContext).denyPermissions(Permission.CHANGE_NETWORK_STATE.androidValue)
        prepareVerification().initiate()
        verify(exactly = 1) { mockedInitListener.onInitializationFailed(any<VerificationException>()) }
    }

    @Test
    fun testFailureInitializationNotifiesListener() {
        val error = mockk<Throwable>()
        every { mockedService.initializeVerification(any()) } returns Calls.failure(error)
        val verification = prepareVerification().apply { initiate() }

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
        every { mockedService.initializeVerification(any()) } returns Calls.response(
            Response.success(correctInitResponse.copy(details = SeamlessInitializationDetails("badUri")))
        )
        val verification = prepareVerification().apply { initiate() }
        mockNetworkAvailable()

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
    fun testManuallyStoppingFinishedVerificationUnregistersNetworkCallbackOnce() {
        // Robolectric's shadow ignores unregistering an unknown callback, so a mock is used
        // to catch the double unregister that throws IllegalArgumentException on a real device.
        val mockedConnectivityManager = mockk<ConnectivityManager>(relaxed = true)
        val callbackSlot = slot<ConnectivityManager.NetworkCallback>()
        every { mockedConnectivityManager.requestNetwork(any(), capture(callbackSlot)) } just Runs
        every { mockedGlobalConfig.context } returns object : ContextWrapper(appContext) {
            override fun getSystemService(name: String): Any? =
                if (name == Context.CONNECTIVITY_SERVICE) mockedConnectivityManager
                else super.getSystemService(name)
        }

        val verification = prepareVerification().apply { initiate() }
        callbackSlot.captured.onAvailable(ShadowNetwork.newInstance(1))
        verification.stop()

        verify(exactly = 1) { mockedVerificationListener.onVerified() }
        verify(exactly = 1) { mockedConnectivityManager.unregisterNetworkCallback(callbackSlot.captured) }
    }

    @Test
    fun testSkipLocalInitializationVerifiesPassedTargetUri() {
        val verification = SeamlessVerificationMethod.Builder()
            .config(
                SeamlessVerificationConfig.Builder()
                    .globalConfig(mockedGlobalConfig)
                    .skipLocalInitialization()
                    .build()
            )
            .initializationListener(mockedInitListener)
            .verificationListener(mockedVerificationListener)
            .build()

        verification.verify(targetUri)
        mockNetworkAvailable()

        verify(exactly = 0) { mockedService.initializeVerification(any()) }
        verify(exactly = 1) { mockedService.verifySeamless(targetUri) }
        verify(exactly = 1) { mockedVerificationListener.onVerified() }
        assertEquals(
            VerificationState.Verification(VerificationStateStatus.SUCCESS),
            verification.verificationState
        )
    }

    // ---- v2 (digital credential) tests ----

    @Test
    fun testV2MissingVpResponseNotifiesFailure() {
        every { mockedService.initializeVerification(any()) } returns
            Calls.response(Response.success(v2InitResponseNoVp))

        prepareVerification(withActivity = true).initiate()

        verify(exactly = 1) {
            mockedVerificationListener.onVerificationFailed(match {
                it is VerificationException && it.message?.contains("vpResponse") == true
            })
        }
    }

    @Test
    fun testV2MissingActivityContextNotifiesFailure() {
        every { mockedService.initializeVerification(any()) } returns
            Calls.response(Response.success(v2InitResponseWithVp))

        prepareVerification(withActivity = false).initiate()

        verify(exactly = 1) {
            mockedVerificationListener.onVerificationFailed(match {
                it is VerificationException && it.message?.contains("Activity") == true
            })
        }
    }

    @Test
    fun testV2CredentialPickerExceptionNotifiesFailure() {
        every { mockedService.initializeVerification(any()) } returns
            Calls.response(Response.success(v2InitResponseWithVp))
        coEvery { mockedCredentialManager.getCredential(any<Context>(), any<GetCredentialRequest>()) } throws
            GetCredentialCancellationException("user cancelled")

        prepareVerification(withActivity = true).initiate()

        verify(exactly = 1) {
            mockedVerificationListener.onVerificationFailed(any<GetCredentialCancellationException>())
        }
    }

    @Test
    fun testV2SuccessfulVerificationCallsCallbackEndpointWithCredential() {
        every { mockedService.initializeVerification(any()) } returns
            Calls.response(Response.success(v2InitResponseWithVp))
        coEvery { mockedCredentialManager.getCredential(any<Context>(), any<GetCredentialRequest>()) } returns
            mockk<GetCredentialResponse> {
                every { credential } returns mockk<DigitalCredential>(relaxed = true) {
                    every { credentialJson } returns testCredentialJson
                }
            }
        every { mockedService.verifySeamlessWithCredential(any()) } returns
            Calls.response(Response.success(mockk<VerificationResponseData>(relaxed = true).apply {
                every { status } returns VerificationStatus.SUCCESSFUL
            }))

        prepareVerification(withActivity = true).initiate()

        verify(exactly = 1) {
            mockedService.verifySeamlessWithCredential(
                body = SeamlessCallbackBody(state = v2VerificationId, operatorToken = testCredentialJson)
            )
        }
        verify(exactly = 1) { mockedVerificationListener.onVerified() }
    }

    @Test
    fun testV2StopCancelsCredentialPickerWithoutNotifyingListener() {
        every { mockedService.initializeVerification(any()) } returns
            Calls.response(Response.success(v2InitResponseWithVp))
        coEvery { mockedCredentialManager.getCredential(any<Context>(), any<GetCredentialRequest>()) } coAnswers {
            awaitCancellation()
        }

        val verification = prepareVerification(withActivity = true).apply { initiate() }
        verification.stop()

        assertEquals(VerificationState.ManuallyStopped, verification.verificationState)
        verify { mockedVerificationListener wasNot Called }
    }

    // ---- v2 (carrier app URL) tests ----

    @Test
    fun testV2AppUrlStartsCarrierAppInsteadOfCredentialManager() {
        every { mockedService.initializeVerification(any()) } returns
            Calls.response(Response.success(v2InitResponseWithAppUrl))

        val verification = prepareVerification().apply { initiate() }

        val startedIntent = Shadows.shadowOf(appContext).nextStartedActivity
        assertEquals(Intent.ACTION_VIEW, startedIntent.action)
        assertEquals(carrierAppUrl, startedIntent.data?.buildUpon()?.clearQuery()?.build()?.toString())
        assertEquals(appInfoJwt, startedIntent.data?.getQueryParameter(appInfoJwtQueryParameterName))
        assertTrue(startedIntent.flags and Intent.FLAG_ACTIVITY_NEW_TASK != 0)
        coVerify(exactly = 0) {
            mockedCredentialManager.getCredential(any<Context>(), any<GetCredentialRequest>())
        }
        assertEquals(
            VerificationState.Verification(VerificationStateStatus.ONGOING),
            verification.verificationState
        )
    }

    @Test
    fun testV2AppUrlCallbackSubmitsOperatorToken() {
        every { mockedService.initializeVerification(any()) } returns
            Calls.response(Response.success(v2InitResponseWithAppUrl))
        every { mockedService.verifySeamlessWithCredential(any()) } returns
            Calls.response(Response.success(mockk<VerificationResponseData>(relaxed = true).apply {
                every { status } returns VerificationStatus.SUCCESSFUL
            }))
        val verification = prepareVerification().apply { initiate() }

        verification.verify(
            "https://test.nv2.verification.api.sinch.com/callback?$appCallbackQueryParameterName=$callbackToken"
        )

        verify(exactly = 1) {
            mockedService.verifySeamlessWithCredential(
                SeamlessCallbackBody(state = v2VerificationId, operatorToken = callbackToken)
            )
        }
        verify(exactly = 1) { mockedVerificationListener.onVerified() }
    }

    @Test
    fun testV2AppUrlMissingMandatoryFieldsNotifiesFailure() {
        every { mockedService.initializeVerification(any()) } returns Calls.response(
            Response.success(
                v2InitResponseWithAppUrl.copy(
                    details = v2InitResponseWithAppUrl.details.copy(appInfoJwt = null)
                )
            )
        )

        val verification = prepareVerification().apply { initiate() }

        verify(exactly = 1) {
            mockedVerificationListener.onVerificationFailed(match {
                it is VerificationException && it.message?.contains("mandatory fields") == true
            })
        }
        assertNull(Shadows.shadowOf(appContext).nextStartedActivity)
        assertEquals(
            VerificationState.Verification(VerificationStateStatus.ERROR),
            verification.verificationState
        )
    }

    @Test
    fun testV2AppUrlCallbackWithoutTokenNotifiesFailure() {
        every { mockedService.initializeVerification(any()) } returns
            Calls.response(Response.success(v2InitResponseWithAppUrl))
        val verification = prepareVerification().apply { initiate() }

        verification.verify("https://test.nv2.verification.api.sinch.com/callback")

        verify(exactly = 1) {
            mockedVerificationListener.onVerificationFailed(match {
                it is VerificationException && it.message?.contains(appCallbackQueryParameterName) == true
            })
        }
        verify(exactly = 0) { mockedService.verifySeamlessWithCredential(any()) }
        assertEquals(
            VerificationState.Verification(VerificationStateStatus.ERROR),
            verification.verificationState
        )
    }

    @Test
    fun testV2AppUrlCallbackErrorNotifiesFailureWithDescription() {
        every { mockedService.initializeVerification(any()) } returns
            Calls.response(Response.success(v2InitResponseWithAppUrl))
        val verification = prepareVerification().apply { initiate() }

        verification.verify(
            "https://test.nv2.verification.api.sinch.com/callback" +
                "?error=access_denied&error_description=User%20cancelled"
        )

        verify(exactly = 1) {
            mockedVerificationListener.onVerificationFailed(match {
                it is VerificationException &&
                    it.message?.contains("access_denied") == true &&
                    it.message?.contains("User cancelled") == true
            })
        }
        verify(exactly = 0) { mockedService.verifySeamlessWithCredential(any()) }
        assertEquals(
            VerificationState.Verification(VerificationStateStatus.ERROR),
            verification.verificationState
        )
    }

    @Test
    fun testV2AppUrlMissingCallbackParameterNameSuggestsCorrectInstance() {
        every { mockedService.initializeVerification(any()) } returns Calls.response(
            Response.success(
                v2InitResponseWithAppUrl.copy(
                    details = v2InitResponseWithAppUrl.details.copy(
                        appCallbackQueryParameterName = null
                    )
                )
            )
        )
        val verification = prepareVerification().apply { initiate() }

        verification.verify("https://test.nv2.verification.api.sinch.com/callback")

        verify {
            mockedVerificationListener.onVerificationFailed(match {
                it is VerificationException && it.message?.contains("same verification instance") == true
            })
        }
    }

    // ---- helpers ----

    private fun prepareVerification(withActivity: Boolean = false) =
        SeamlessVerificationMethod.Builder()
            .config(
                SeamlessVerificationConfig.Builder()
                    .globalConfig(mockedGlobalConfig)
                    .number(testPhone)
                    .also { if (withActivity) it.activity(mockedActivity) }
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
}
