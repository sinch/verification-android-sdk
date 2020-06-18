package com.sinch.verification.seamless

import com.sinch.verification.seamless.config.SeamlessVerificationConfig
import com.sinch.verificationcore.config.general.GlobalConfig
import com.sinch.verificationcore.config.method.VerificationMethodProperties
import com.sinch.verificationcore.verification.VerificationLanguage
import io.mockk.mockk
import org.junit.Assert
import org.junit.Test

class SeamlessVerificationConfigBuilderTest {

    private val globalConfig = mockk<GlobalConfig>(relaxed = true)

    @Test
    fun testBuilderPropertiesAllSet() {
        val testNumber = "+48123456789"
        val honourEarly = true
        val customParam = "testcustom"
        val refParam = "ref"

        val config = SeamlessVerificationConfig(
            globalConfig = globalConfig,
            number = testNumber,
            honourEarlyReject = honourEarly,
            custom = customParam,
            reference = refParam
        )

        val builtConfig = SeamlessVerificationConfig.Builder()
            .globalConfig(globalConfig)
            .number(testNumber)
            .honourEarlyReject(honourEarly)
            .custom(customParam)
            .reference(refParam)
            .build()

        testSeamlessConfigParamsEquality(config, builtConfig)
    }

    @Test
    fun testRequiredBuilderPropertiesSet() {
        val testNumber = "+48123456789"
        val config = SeamlessVerificationConfig(globalConfig, testNumber)
        val builtConfig =
            SeamlessVerificationConfig.Builder().globalConfig(globalConfig).number(testNumber)
                .build()
        testSeamlessConfigParamsEquality(config, builtConfig)
    }

    @Test
    fun testConfigBuiltUsingVerificationProperties() {
        val testNumber = "+12456789789"
        val honourEarly = false
        val customParam = null
        val refParam = "myRef"

        val config = SeamlessVerificationConfig(
            globalConfig = globalConfig,
            number = testNumber,
            honourEarlyReject = honourEarly,
            custom = customParam,
            reference = refParam
        )

        val builtConfig = SeamlessVerificationConfig.Builder().globalConfig(globalConfig)
            .withVerificationProperties(
                verificationMethodProperties = object : VerificationMethodProperties {
                    override val number: String = testNumber
                    override val custom: String? = customParam
                    override val reference: String? = refParam
                    override val honourEarlyReject: Boolean = honourEarly
                    override val acceptedLanguages: List<VerificationLanguage> = emptyList()
                }
            ).build()

        testSeamlessConfigParamsEquality(config, builtConfig)
    }


    private fun testSeamlessConfigParamsEquality(
        config1: SeamlessVerificationConfig,
        config2: SeamlessVerificationConfig
    ) {
        Assert.assertEquals(config1.number, config2.number)
        Assert.assertEquals(config1.honourEarlyReject, config2.honourEarlyReject)
        Assert.assertEquals(config1.custom, config2.custom)
        Assert.assertEquals(config1.reference, config2.reference)
        Assert.assertEquals(config1.acceptedLanguages, config2.acceptedLanguages)
    }

}