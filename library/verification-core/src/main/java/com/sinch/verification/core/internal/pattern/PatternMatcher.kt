package com.sinch.verification.core.internal.pattern

import java.util.regex.Pattern

/**
 * General logic for checking if given code matches a template.
 * @param template String used to create a [Pattern] instance.
 * @param patternFactory Factory used to create a [Pattern] instance based on template parameter.
 */
abstract class PatternMatcher(
    template: String,
    patternFactory: PatternFactory
) : PatternHandler(template, patternFactory) {

    /**
     * Checks if given message matches the template of current matcher.
     * @param message Message to be checked.
     * @return Boolean indicating if match is found.
     */
    fun matches(message: String): Boolean =
        pattern.matcher(message).matches()

}