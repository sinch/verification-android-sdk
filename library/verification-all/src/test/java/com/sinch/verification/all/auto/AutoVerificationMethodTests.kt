package com.sinch.verification.all.auto

import android.app.Application
import android.content.Intent
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.sinch.verification.all.auto.config.AutoVerificationConfig
import com.sinch.verification.all.auto.initialization.AutoInitializationListener
import com.sinch.verification.all.auto.initialization.AutoInitializationResponseData
import com.sinch.verification.all.auto.verification.events.AutoVerificationEvent
import com.sinch.verification.core.config.general.GlobalConfig
import com.sinch.verification.core.internal.VerificationMethodType
import com.sinch.verification.core.internal.VerificationState
import com.sinch.verification.core.internal.VerificationStateStatus
import com.sinch.verification.core.internal.VerificationStatus
import com.sinch.verification.core.internal.error.VerificationException
import com.sinch.verification.core.verification.response.VerificationListener
import com.sinch.verification.core.verification.response.VerificationResponseData
import com.sinch.verification.seamless.SeamlessCallbackBody
import com.sinch.verification.utils.permission.Permission
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import retrofit2.Response
import retrofit2.mock.Calls

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class AutoVerificationMethodTests {

    companion object {
        internal object Constants {
            const val phone = "+48123456789"
            const val autoVerificationId = "auto-test-id"
            const val seamlessChildId = "seamless-child-id"
            const val calloutChildId = "callout-child-id"
            const val carrierAppUrl = "https://carrier.example.com/verify"
            const val appInfoJwt = "test-app-info-jwt"
            const val appInfoJwtQueryParameterName = "app_info"
            const val appCallbackQueryParameterName = "operator_token"
            const val callbackToken = "test-operator-token"
        }

        private val json = Json { ignoreUnknownKeys = true }

        private fun v2AutoInitResponse(seamlessDetailsJson: String): AutoInitializationResponseData =
            json.decodeFromString(
                """{"id":"${Constants.autoVerificationId}","method":"auto",""" +
                    """"auto":{"methodsOrder":["seamless","callout"]},""" +
                    """"callout":{"subVerificationId":"${Constants.calloutChildId}"},""" +
                    """"seamless":$seamlessDetailsJson}"""
            )

        private fun v2AppUrlDetailsJson(includeVpResponse: Boolean = false): String =
            "{\"subVerificationId\":\"${Constants.seamlessChildId}\",\"version\":\"v2\"," +
                "\"androidAppUrl\":\"${Constants.carrierAppUrl}\"," +
                "\"appInfoJwt\":\"${Constants.appInfoJwt}\"," +
                "\"appInfoJwtQueryParameterName\":\"${Constants.appInfoJwtQueryParameterName}\"," +
                "\"appCallbackQueryParameterName\":\"${Constants.appCallbackQueryParameterName}\"" +
                (if (includeVpResponse) ",\"vpResponse\":{\"nonce\":\"test\"}" else "") +
                "}"

        private fun v2VpResponseOnlyDetailsJson(): String =
            "{\"subVerificationId\":\"${Constants.seamlessChildId}\",\"version\":\"v2\",\"nonce\":\"test-nonce\"}"
    }

    private val appContext = ApplicationProvider.getApplicationContext<Application>()

    private val mockedService = mockk<AutoVerificationService>(relaxed = true)

    private val mockedGlobalConfig = spyk<GlobalConfig> {
        every { context } returns (appContext)
        every { retrofit } returns mockk {
            every { create(AutoVerificationService::class.java) } returns mockedService
        }
    }

    @MockK
    lateinit var mockedInitListener: AutoInitializationListener

    @MockK
    lateinit var mockedVerificationListener: VerificationListener

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxed = true)
        Shadows.shadowOf(appContext).grantPermissions(Permission.READ_CALL_LOG.androidValue)
    }

    private fun prepareVerification() =
        AutoVerificationMethod.Builder.instance
            .config(
                AutoVerificationConfig.Builder.instance
                    .globalConfig(mockedGlobalConfig)
                    .number(Constants.phone)
                    .build()
            )
            .initializationListener(mockedInitListener)
            .verificationListener(mockedVerificationListener)
            .build()

    private fun initiateWith(response: AutoInitializationResponseData) =
        prepareVerification().apply {
            every { mockedService.initializeVerification(any()) } returns
                Calls.response(Response.success(response))
            initiate()
        }

    @Test
    fun testV2AppUrlStartsCarrierApp() {
        initiateWith(v2AutoInitResponse(v2AppUrlDetailsJson()))

        val startedIntent = Shadows.shadowOf(appContext).nextStartedActivity
        assertEquals(Intent.ACTION_VIEW, startedIntent.action)
        assertEquals(
            Constants.carrierAppUrl,
            startedIntent.data?.buildUpon()?.clearQuery()?.build()?.toString()
        )
        assertEquals(
            Constants.appInfoJwt,
            startedIntent.data?.getQueryParameter(Constants.appInfoJwtQueryParameterName)
        )
        assertTrue(startedIntent.flags and Intent.FLAG_ACTIVITY_NEW_TASK != 0)
        verify(exactly = 0) { mockedVerificationListener.onVerified() }
        verify(exactly = 0) { mockedVerificationListener.onVerificationFailed(any()) }
    }

    @Test
    fun testV2AppUrlTakesPrecedenceOverVpResponse() {
        initiateWith(v2AutoInitResponse(v2AppUrlDetailsJson(includeVpResponse = true)))

        val startedIntent = Shadows.shadowOf(appContext).nextStartedActivity
        assertEquals(Intent.ACTION_VIEW, startedIntent.action)
        verify(exactly = 0) { mockedService.verifySeamlessWithCredential(any()) }
        verify(exactly = 0) { mockedVerificationListener.onVerified() }
        verify(exactly = 0) { mockedVerificationListener.onVerificationFailed(any()) }
    }

    @Test
    fun testV2WithoutAppUrlAndVpResponseNotifiesSeamlessFailure() {
        val verification = initiateWith(v2AutoInitResponse(v2VpResponseOnlyDetailsJson()))

        verify(exactly = 1) {
            mockedVerificationListener.onVerificationEvent(
                match { it is AutoVerificationEvent.SubMethodFailedEvent }
            )
        }
        verify(exactly = 1) {
            mockedVerificationListener.onVerificationFailed(
                match {
                    it is VerificationException &&
                        it.message?.contains("missing androidAppUrl and vpResponse") == true
                }
            )
        }
        assertNull(Shadows.shadowOf(appContext).nextStartedActivity)
        assertEquals(
            VerificationState.Verification(VerificationStateStatus.ERROR),
            verification.verificationState
        )
    }

    @Test
    fun testV2AppUrlCallbackReportsOperatorToken() {
        every { mockedService.verifySeamlessWithCredential(any()) } returns
            Calls.response(
                Response.success(
                    mockk<VerificationResponseData>(relaxed = true).apply {
                        every { status } returns VerificationStatus.SUCCESSFUL
                    }
                )
            )
        val verification = initiateWith(v2AutoInitResponse(v2AppUrlDetailsJson()))

        verification.verify(
            "https://test.nv2.verification.api.sinch.com/callback" +
                "?${Constants.appCallbackQueryParameterName}=${Constants.callbackToken}"
        )

        verify(exactly = 1) {
            mockedService.verifySeamlessWithCredential(
                SeamlessCallbackBody(
                    state = Constants.seamlessChildId,
                    operatorToken = Constants.callbackToken
                )
            )
        }
        verify(exactly = 1) { mockedVerificationListener.onVerified() }
    }

    @Test
    fun testVerifyWithCodeWhileAppUrlFlowActiveIsNotRoutedToSeamlessStrategy() {
        every { mockedService.verifyById(any(), any()) } returns
            Calls.response(
                Response.success(
                    mockk<VerificationResponseData>(relaxed = true).apply {
                        every { status } returns VerificationStatus.SUCCESSFUL
                    }
                )
            )
        val verification = initiateWith(v2AutoInitResponse(v2AppUrlDetailsJson()))

        verification.verify("1234", VerificationMethodType.CALLOUT)

        verify(exactly = 0) { mockedService.verifySeamlessWithCredential(any()) }
        verify(exactly = 1) {
            mockedService.verifyById(
                Constants.calloutChildId,
                any()
            )
        }
    }

    @Test
    fun testV2AppUrlCallbackErrorNotifiesFailure() {
        val verification = initiateWith(v2AutoInitResponse(v2AppUrlDetailsJson()))

        verification.verify(
            "https://test.nv2.verification.api.sinch.com/callback" +
                "?error=access_denied&error_description=User%20cancelled"
        )

        verify(exactly = 1) {
            mockedVerificationListener.onVerificationFailed(
                match {
                    it is VerificationException &&
                        it.message?.contains("access_denied") == true &&
                        it.message?.contains("User cancelled") == true
                }
            )
        }
        verify(exactly = 0) { mockedService.verifySeamlessWithCredential(any()) }
        assertEquals(
            VerificationState.Verification(VerificationStateStatus.ERROR),
            verification.verificationState
        )
    }
}
