package com.sinch.verification.flashcall

import android.os.Build
import com.sinch.verification.flashcall.verification.matcher.FlashCallPatternMatcher
import com.sinch.verification.core.internal.error.CodeInterceptionException
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(
    RobolectricTestRunner::class
)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class FlashCallPatternMatcherTests {

    companion object {
        const val testPattern1 = "(.*)70123(.*)"
        const val testPattern2 = "(.*)1234"
        const val testPatternWildcard = "(.*)"
        const val testPatternInvalid = "(.*4544456(.*)"
        const val exactMatchPattern = "+48123456789"
    }

    @Test
    fun testPattern1() {
        val matcher1 = FlashCallPatternMatcher(testPattern1)
        listOf(
            "+48701230",
            "+48007012312131230",
            "+48515170123",
            "70123",
            "7012370123"
        ).let {
            testPatternDoesMatch(matcher1, it)
        }

        listOf(
            "",
            "+481234564",
            "70120",
            "+485097012245"
        ).let {
            testPatternDoesNotMatch(matcher1, it)
        }
    }

    @Test
    fun testPattern2() {
        val matcher2 = FlashCallPatternMatcher(testPattern2)

        listOf(
            "1234",
            "+48007012312131234",
            "701234"
        ).let {
            testPatternDoesMatch(matcher2, it)
        }

        listOf(
            "",
            "+481234564",
            "70120",
            "+485097012245"
        ).let {
            testPatternDoesNotMatch(matcher2, it)
        }

    }

    @Test
    fun testWildcardPattern() {
        val matcher3 = FlashCallPatternMatcher(testPatternWildcard)

        listOf(
            "1234",
            "",
            "a",
            "+48555551234",
            "123",
            "xyz"
        ).let {
            testPatternDoesMatch(matcher3, it)
        }
    }

    @Test
    fun testExactPattern() {
        val matcher4 = FlashCallPatternMatcher(exactMatchPattern)

        assertTrue(matcher4.matches("+48123456789"))

        listOf(
            "1234",
            "",
            "a",
            "00+48123456789",
            "+481234567890",
            "+48555551234",
            "123",
            "xyz"
        ).let {
            testPatternDoesNotMatch(matcher4, it)
        }

    }

    @Test(expected = CodeInterceptionException::class)
    fun testMalformedPatternShouldThrow() {
        val matcher3 = FlashCallPatternMatcher(testPatternInvalid)
    }

    private fun testPatternDoesMatch(matcher: FlashCallPatternMatcher, cases: List<String>) {
        cases.forEach { assertTrue(matcher.matches(it)) }
    }

    private fun testPatternDoesNotMatch(matcher: FlashCallPatternMatcher, cases: List<String>) {
        cases.forEach { assertFalse(matcher.matches(it)) }
    }
}
