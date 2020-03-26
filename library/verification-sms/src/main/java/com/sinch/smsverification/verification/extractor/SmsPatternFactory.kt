package com.sinch.smsverification.verification.extractor

import com.sinch.verificationcore.internal.error.CodeInterceptionException
import com.sinch.verificationcore.internal.pattern.PatternFactory
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException

class SmsPatternFactory : PatternFactory {

    companion object {
        const val CODE_PATTERN = "{{CODE}}"
    }

    override fun create(template: String): Pattern {
        if (!template.contains(CODE_PATTERN)) {
            throw CodeInterceptionException("Incorrect template: $template");
        }
        val escapedTemplate = Pattern.quote(template)
            .replace(CODE_PATTERN, "\\E$CODE_PATTERN\\Q")
        try {
            return Pattern.compile(escapedTemplate.replace(CODE_PATTERN, "(.+)"))
        } catch (e: PatternSyntaxException) {
            throw CodeInterceptionException("Failed to compile pattern: $escapedTemplate error: ${e.message}")
        }
    }

}