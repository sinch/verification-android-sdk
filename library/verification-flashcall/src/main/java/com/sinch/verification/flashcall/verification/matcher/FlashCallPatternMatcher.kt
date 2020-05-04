package com.sinch.verification.flashcall.verification.matcher

import com.sinch.verification.flashcall.initialization.FlashCallInitializationDetails
import com.sinch.verificationcore.internal.pattern.PatternMatcher

/**
 * [PatternMatcher] that checks if incoming call matches pattern defined by [FlashCallInitializationDetails.cliFilter].
 */
class FlashCallPatternMatcher(template: String) : PatternMatcher(
    template = template,
    patternFactory = FlashCallPatternFactory()
)