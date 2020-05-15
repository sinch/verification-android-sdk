package com.sinch.verification.flashcall.verification.matcher

import com.sinch.verification.flashcall.initialization.FlashCallInitializationDetails
import com.sinch.verificationcore.internal.error.CodeInterceptionException
import com.sinch.verificationcore.internal.pattern.PatternFactory
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException

/**
 * [PatternFactory] that creates [Pattern] instance based on [FlashCallInitializationDetails.cliFilter] field.
 */
internal class FlashCallPatternFactory : PatternFactory {

    override fun create(template: String): Pattern {
        val escapedTemplate = Pattern.quote(template)
            .replace("(", "\\E(")
            .replace(")", ")\\Q")

        try {
            return Pattern.compile(escapedTemplate)
        } catch (e: PatternSyntaxException) {
            throw CodeInterceptionException("Failed to compile pattern: $escapedTemplate, error: ${e.message}")
        }
    }

}