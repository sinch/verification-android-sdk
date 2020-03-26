package com.sinch.verificationcore.internal.pattern

abstract class CodeExtractor(
    template: String,
    patternFactory: PatternFactory
) : PatternHandler(template, patternFactory) {

    fun extract(message: String): String? {
        val matcher = pattern.matcher(message)
        return if (matcher.find()) matcher.group(1) else null
    }

}