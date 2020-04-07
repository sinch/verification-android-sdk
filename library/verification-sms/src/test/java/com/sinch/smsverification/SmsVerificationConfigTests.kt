package com.sinch.smsverification

import android.os.Build
import com.sinch.smsverification.config.SmsVerificationConfig
import com.sinch.verificationcore.config.general.GlobalConfig
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(
    RobolectricTestRunner::class
)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class SmsVerificationConfigTests {

    companion object {
        const val testNumber = "+48123456789"
    }

    @MockK(relaxed = true)
    lateinit var globalConfig: GlobalConfig

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun testDefaultConstructorMatches() {
        val builtConfig = SmsVerificationConfig.Builder.instance.globalConfig(globalConfig).number(
            testNumber
        ).build()
        checkOptionalFieldsMatches(builtConfig, SmsVerificationConfig(globalConfig, testNumber))
    }

    @Test
    fun testFullBuilderMatches() {
        val custom = "custom"
        val honourEarly = false
        val maxTimeout = 10000L
        val appHash = "appHash"

        checkOptionalFieldsMatches(
            config1 = SmsVerificationConfig.Builder.instance
                .globalConfig(globalConfig)
                .number(testNumber)
                .appHash(appHash)
                .maxTimeout(maxTimeout)
                .custom(custom)
                .honourEarlyReject(honourEarly)
                .build(),
            config2 = SmsVerificationConfig.Builder.instance
                .globalConfig(globalConfig)
                .number(testNumber)
                .honourEarlyReject(honourEarly)
                .custom(custom)
                .maxTimeout(maxTimeout)
                .appHash(appHash)
                .build()
        )
    }

    private fun checkOptionalFieldsMatches(
        config1: SmsVerificationConfig,
        config2: SmsVerificationConfig
    ) {
        Assert.assertEquals(config1.appHash, config2.appHash)
        Assert.assertEquals(config1.custom, config2.custom)
        Assert.assertEquals(config1.honourEarlyReject, config2.honourEarlyReject)
        Assert.assertEquals(config1.maxTimeout, config2.maxTimeout)
    }
}