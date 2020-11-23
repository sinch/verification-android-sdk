package com.sinch.verification.core.internal.pattern

import java.util.regex.Pattern

/**
 * Interface used for creating [Pattern] instances based on passed template.
 */
interface PatternFactory {

    /**
     * Creates a [Pattern] instance based on given template.
     */
    fun create(template: String): Pattern
}