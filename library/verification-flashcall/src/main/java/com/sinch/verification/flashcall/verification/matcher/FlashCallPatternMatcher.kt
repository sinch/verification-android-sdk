package com.sinch.verification.flashcall.verification.matcher

import com.sinch.verificationcore.internal.pattern.PatternMatcher

class FlashCallPatternMatcher(template: String) : PatternMatcher(
    template = template,
    patternFactory = FlashCallPatternFactory()
)