package com.sinch.verificationcore.internal.pattern

import java.util.regex.Pattern

abstract class PatternHandler(template: String, patternFactory: PatternFactory) {
    internal val pattern: Pattern = patternFactory.create(template)
}