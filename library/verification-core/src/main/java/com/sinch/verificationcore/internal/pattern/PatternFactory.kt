package com.sinch.verificationcore.internal.pattern

import java.util.regex.Pattern

interface PatternFactory {
    fun create(template: String): Pattern
}