package com.sinch.verification.seamless.util

import android.app.Application
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.sinch.verification.core.config.general.GlobalConfig
import com.sinch.verification.seamless.SeamlessHeaderInterceptor
import com.sinch.verification.seamless.config.SeamlessVerificationConfig
import io.mockk.every
import io.mockk.mockk
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import retrofit2.Retrofit
import javax.net.SocketFactory

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class SeamlessRetrofitProviderTests {

    private lateinit var globalConfig: GlobalConfig
    private lateinit var baseRetrofit: Retrofit
    private lateinit var appContext: Application
    private val defaultBaseUrl = "https://test.api.sinch.com/"

    @Before
    fun setup() {
        appContext = ApplicationProvider.getApplicationContext()
        
        baseRetrofit = Retrofit.Builder()
            .baseUrl(defaultBaseUrl.toHttpUrl())
            .client(OkHttpClient())
            .build()

        globalConfig = mockk<GlobalConfig>(relaxed = true) {
            every { context } returns appContext
            every { retrofit } returns baseRetrofit
            every { socketFactoryRetrofit(any()) } returns baseRetrofit
        }
    }

    @Test
    fun testIndiaNumberRoutesToIndiaEndpoint() {
        val config = SeamlessVerificationConfig.Builder()
            .globalConfig(globalConfig)
            .number("+911234567890")
            .build()

        val socketFactory = SocketFactory.getDefault()
        val retrofit = SeamlessRetrofitProvider.buildVerificationServiceRetrofitWithSocket(
            config,
            socketFactory
        )

        val baseUrl = retrofit.baseUrl().toString()
        // Should contain India endpoint path
        assertTrue("Base URL should contain verification/v1/", baseUrl.contains("verification/v1/"))
    }

    @Test
    fun testNonIndiaNumberUsesDefaultEndpoint() {
        val config = SeamlessVerificationConfig.Builder()
            .globalConfig(globalConfig)
            .number("+48123456789")
            .build()

        val socketFactory = SocketFactory.getDefault()
        val retrofit = SeamlessRetrofitProvider.buildVerificationServiceRetrofitWithSocket(
            config,
            socketFactory
        )

        val baseUrl = retrofit.baseUrl().toString()
        // Should use the original base URL
        assertEquals(defaultBaseUrl, baseUrl)
    }

    @Test
    fun testInterceptorAddedForIndiaNumber() {
        val retrofit = SeamlessRetrofitProvider.buildSeamlessVerificationRetrofit(
            baseRetrofit,
            "+911234567890"
        )

        val client = retrofit.callFactory() as? OkHttpClient
        assertTrue("Client should be OkHttpClient", client is OkHttpClient)

        val interceptors = client!!.interceptors
        // Check if SeamlessHeaderInterceptor is present
        val hasSeamlessHeaderInterceptor = interceptors.any { it is SeamlessHeaderInterceptor }
        assertTrue(
            "SeamlessHeaderInterceptor should be added for India numbers",
            hasSeamlessHeaderInterceptor
        )
    }

    @Test
    fun testInterceptorNotAddedForNonIndiaNumber() {
        val retrofit = SeamlessRetrofitProvider.buildSeamlessVerificationRetrofit(
            baseRetrofit,
            "+48123456789"
        )

        val client = retrofit.callFactory() as? OkHttpClient
        assertTrue("Client should be OkHttpClient", client is OkHttpClient)

        val interceptors = client!!.interceptors
        // Check that SeamlessHeaderInterceptor is NOT present
        val hasSeamlessHeaderInterceptor = interceptors.any { it is SeamlessHeaderInterceptor }
        assertFalse(
            "SeamlessHeaderInterceptor should NOT be added for non-India numbers",
            hasSeamlessHeaderInterceptor
        )
    }
}

