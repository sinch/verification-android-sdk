package com.sinch.verification.utils

import android.os.Build
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.Locale

@RunWith(
    RobolectricTestRunner::class
)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class AcceptedLanguagesParsingTests {

    @Test
    fun testSingleLanguageListElement() {
        val singleLocale = listOf(Locale("pl","PL"))
        Assert.assertEquals("pl-PL", singleLocale.asLanguagesString())
    }

    @Test
    fun testMultipleLanguageListElements() {
        val languageList =
        listOf(
            Locale("pl","PL"),
            Locale("fr","CA"),
            Locale("is", "IS"),
            Locale("pl")
        )
        Assert.assertEquals("pl-PL,fr-CA,is-IS,pl", languageList.asLanguagesString())
    }

    @Test
    fun testEmptyLanguageList() {
        val emptyList = emptyList<Locale>()
        Assert.assertEquals(null, emptyList.asLanguagesString())
    }
}