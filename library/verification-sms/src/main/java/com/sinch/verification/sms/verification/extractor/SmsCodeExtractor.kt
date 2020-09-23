package com.sinch.verification.sms.verification.extractor

import com.sinch.verification.core.internal.pattern.CodeExtractor

/**
 * [CodeExtractor] that extracts verification codes from sms messages.
 */
class SmsCodeExtractor(template: String) : CodeExtractor(
    template = template,
    patternFactory = SmsPatternFactory()
)