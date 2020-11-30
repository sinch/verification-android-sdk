package com.sinch.smsverification

import android.os.Build
import com.sinch.verification.core.config.general.GlobalConfig
import com.sinch.verification.core.config.method.VerificationMethodProperties
import com.sinch.verification.core.verification.VerificationLanguage
import com.sinch.verification.sms.SmsVerificationService
import com.sinch.verification.sms.config.SmsVerificationConfig
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
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
        every { globalConfig.retrofit } returns mockk {
            every { create(SmsVerificationService::class.java) } returns mockk()
        }
    }

    @Test
    fun testDefaultConstructorMatches() {
        val builtConfig = SmsVerificationConfig.Builder.instance.globalConfig(globalConfig).number(
            testNumber
        ).build()
        checkConfigFieldsMatches(builtConfig, SmsVerificationConfig(globalConfig, testNumber))
    }

    @Test
    fun testVerificationMethodPropertiesInitializationMatchesManual() {
        val custom = "custom"
        val honourEarly = false
        val reference = "reference"
        val acceptedLanguages = emptyList<VerificationLanguage>()

        val verificationMethodProperties = object : VerificationMethodProperties {
            override val number: String = testNumber
            override val custom: String = custom
            override val reference: String = reference
            override val honourEarlyReject: Boolean = honourEarly
            override val acceptedLanguages: List<VerificationLanguage> = acceptedLanguages
        }

        val configWithVerificationParameters = SmsVerificationConfig.Builder()
            .globalConfig(globalConfig)
            .withVerificationProperties(verificationMethodProperties)
            .build()

        val manuallyBuiltConfig = SmsVerificationConfig.Builder()
            .globalConfig(globalConfig)
            .number(testNumber)
            .custom(custom)
            .reference(reference)
            .honourEarlyReject(honourEarly)
            .acceptedLanguages(acceptedLanguages)
            .build()

        checkConfigFieldsMatches(configWithVerificationParameters, manuallyBuiltConfig)
    }

    @Test
    fun testMinimalVerificationParametersMatchesDefault() {
        val verificationMethodProperties = object : VerificationMethodProperties {
            override val number: String = testNumber
            override val custom: String? = null
            override val reference: String? = null
            override val honourEarlyReject: Boolean = true
            override val acceptedLanguages: List<VerificationLanguage> = emptyList()
        }

        val configWithVerificationParameters = SmsVerificationConfig.Builder()
            .globalConfig(globalConfig)
            .withVerificationProperties(verificationMethodProperties)
            .build()

        val manuallyBuiltConfig = SmsVerificationConfig.Builder()
            .globalConfig(globalConfig)
            .number(testNumber)
            .build()

        checkConfigFieldsMatches(configWithVerificationParameters, manuallyBuiltConfig)
    }

    @Test
    fun testFullBuilderMatches() {
        val custom = "custom"
        val honourEarly = false
        val appHash = "appHash"

        checkConfigFieldsMatches(
            config1 = SmsVerificationConfig.Builder.instance
                .globalConfig(globalConfig)
                .number(testNumber)
                .appHash(appHash)
                .custom(custom)
                .honourEarlyReject(honourEarly)
                .build(),
            config2 = SmsVerificationConfig.Builder.instance
                .globalConfig(globalConfig)
                .number(testNumber)
                .honourEarlyReject(honourEarly)
                .custom(custom)
                .appHash(appHash)
                .build()
        )
    }

    private fun checkConfigFieldsMatches(
        config1: SmsVerificationConfig,
        config2: SmsVerificationConfig
    ) {
        Assert.assertEquals(config1.appHash, config2.appHash)
        Assert.assertEquals(config1.custom, config2.custom)
        Assert.assertEquals(config1.honourEarlyReject, config2.honourEarlyReject)
        Assert.assertEquals(config1.reference, config2.reference)
        Assert.assertEquals(config1.acceptedLanguages, config2.acceptedLanguages)
        Assert.assertEquals(config1.number, config2.number)
    }
}