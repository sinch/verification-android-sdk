package com.sinch.smsverification.verification.extractor

import com.sinch.verificationcore.internal.pattern.CodeExtractor

class SmsCodeExtractor(template: String) : CodeExtractor(
    template = template,
    patternFactory = SmsPatternFactory()
)