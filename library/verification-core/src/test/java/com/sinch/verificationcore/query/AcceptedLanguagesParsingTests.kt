package com.sinch.verificationcore.query

import com.sinch.verification.core.verification.VerificationLanguage
import com.sinch.verification.core.verification.asLanguagesString
import org.junit.Assert
import org.junit.Test

class AcceptedLanguagesParsingTests {

    @Test
    fun testSingleLanguageListElement() {
        val singleLocale = listOf(VerificationLanguage("pl", "PL"))
        Assert.assertEquals("pl-PL", singleLocale.asLanguagesString())
    }

    @Test
    fun testMultipleLanguageListElements() {
        val languageList =
            listOf(
                VerificationLanguage("pl", "PL"),
                VerificationLanguage("fr", "CA"),
                VerificationLanguage("is", "IS"),
                VerificationLanguage("pl")
            )
        Assert.assertEquals("pl-PL,fr-CA,is-IS,pl", languageList.asLanguagesString())
    }

    @Test
    fun testEmptyLanguageList() {
        val emptyList = emptyList<VerificationLanguage>()
        Assert.assertEquals(null, emptyList.asLanguagesString())
    }

    @Test
    fun testMultipleLanguagesWithWeightList() {
        val languageList =
            listOf(
                VerificationLanguage("pl", "PL"),
                VerificationLanguage("fr", "CA", 0.5),
                VerificationLanguage("is", "IS"),
                VerificationLanguage("pl")
            )
        Assert.assertEquals("pl-PL,fr-CA;q=0.5,is-IS,pl", languageList.asLanguagesString())
    }

    @Test
    fun testMultipleLanguagesWithWeightAllList() {
        val languageList =
            listOf(
                VerificationLanguage("pl", "PL", 0.0),
                VerificationLanguage("fr", "CA", 0.5),
                VerificationLanguage("is", "IS", 1.0),
                VerificationLanguage("pl", "DE", 0.3333)
            )
        Assert.assertEquals(
            "pl-PL;q=0,fr-CA;q=0.5,is-IS;q=1,pl-DE;q=0.333",
            languageList.asLanguagesString()
        )
    }

}