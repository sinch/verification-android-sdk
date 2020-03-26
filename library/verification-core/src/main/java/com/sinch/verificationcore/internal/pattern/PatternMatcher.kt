package com.sinch.verificationcore.internal.pattern

abstract class PatternMatcher(
    template: String,
    patternFactory: PatternFactory
) : PatternHandler(template, patternFactory) {

    fun matches(message: String): Boolean =
        pattern.matcher(message).matches()

}