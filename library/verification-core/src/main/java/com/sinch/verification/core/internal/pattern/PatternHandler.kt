package com.sinch.verification.core.internal.pattern

import java.util.regex.Pattern

/**
 * Common logic for classes using [Pattern] instances created with given regular expression.
 */
abstract class PatternHandler(template: String, patternFactory: PatternFactory) {
    internal val pattern: Pattern = patternFactory.create(template)
}