package com.sinch.verification.core.internal.pattern

/**
 * General logic for extracting code based on regular expression.
 * @param template Expression defining place where to find a verification code in given message.
 * @param patternFactory Factory used to create a Pattern instance.
 */
abstract class CodeExtractor(
    template: String,
    patternFactory: PatternFactory
) : PatternHandler(template, patternFactory) {

    /**
     * Extracts the code from the given message.
     * @param message String from which the code should be extracted based on passed template.
     * @return First found match or null if no matches were found.
     */
    fun extract(message: String): String? {
        val matcher = pattern.matcher(message)
        return if (matcher.find()) matcher.group(1) else null
    }

}