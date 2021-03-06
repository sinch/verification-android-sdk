@file:Suppress("SpellCheckingInspection")

package com.sinch.smsverification

import com.sinch.verification.core.internal.error.CodeInterceptionException
import com.sinch.verification.sms.verification.extractor.SmsCodeExtractor
import org.junit.Assert
import org.junit.Test

class SmsCodeExtractorTests {

    companion object {
        const val invalidTemplate = "Your code is CODE"
        const val template1 = SmsTemplates.exampleSimple1
        const val template2 = "śś™™€€Ńįįį {{CODE}}."
        const val template3 = """Your code is 
            {{CODE}}"""
        const val template4 = "{{CODE}} Your code is {{CODE}}"
        const val template4JustCode = "{{CODE}}"
    }

    @Test(expected = CodeInterceptionException::class)
    fun testInvalidTemplateThrowsException() {
        SmsCodeExtractor(invalidTemplate)
    }

    @Test
    fun testSimpleSingleLineTemplate1() {
        val extractor = SmsCodeExtractor(template1)
        val testCases = mapOf(
            "Your code is abc" to "abc",
            "1234 code" to null,
            "Yourcodeis1234" to null,
            "1234" to null,
            "Your code is 123456789" to "123456789",
            "Your code is 1.23456789" to "1.23456789",
            "Your code is /&(#\"asfasf." to "/&(#\"asfasf."
        )
        testCases.forEach {
            Assert.assertEquals(it.value, extractor.extract(it.key))
        }
    }

    @Test
    fun testSingleLineTemplate2() {
        val extractor = SmsCodeExtractor(template2)
        val testCases = mapOf(
            "śś™™€€Ńįįį abc." to "abc",
            "1234 code" to null,
            "Yourcodeis1234" to null,
            "1234" to null,
            "śś™™€€Ńįįį 123456789." to "123456789",
            "śś™™€€Ńįįį 123456789" to null,
            "śś™™€€Ńįįį /&(#\"asfasf.." to "/&(#\"asfasf."
        )
        testCases.forEach {
            Assert.assertEquals(it.value, extractor.extract(it.key))
        }
    }

    @Test
    fun testMultiLineTemplate() {
        val extractor = SmsCodeExtractor(template3)
        val testCases = mapOf(
            """Your code is 
            abc""" to "abc",
            """Your code is abc""" to null,
            """Your code is 
            abc.""" to "abc.",
            """Your code is 
            1.23456789""" to "1.23456789"
        )
        testCases.forEach {
            Assert.assertEquals(it.value, extractor.extract(it.key))
        }
    }

    @Test
    fun testMultipleCodeBlocks() {
        val extractor = SmsCodeExtractor(template4)
        val testCases = mapOf(
            """abc Your code is abc""" to "abc",
            """cba Your code is abc""" to "cba"
        )
        testCases.forEach {
            Assert.assertEquals(it.value, extractor.extract(it.key))
        }
    }

    @Test
    fun testJustCodeTemplate() {
        val extractor = SmsCodeExtractor(template4JustCode)
        val testCases = mapOf(
            "1234" to "1234",
            "aaaaaa" to "aaaaaa",
            "a" to "a"
        )
        testCases.forEach {
            Assert.assertEquals(it.value, extractor.extract(it.key))
        }
    }

    @Test
    fun testEmptyCodeReturnsNull() {
        val extractor = SmsCodeExtractor(template1)
        val emptyCodeMessage = "Your code is "
        Assert.assertEquals(null, extractor.extract(emptyCodeMessage))
    }

    @Test
    fun testEmptyCode() {
        val extractor = SmsCodeExtractor(template1)
        Assert.assertEquals(null, extractor.extract("Your code is "))
    }
}