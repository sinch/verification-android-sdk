package com.sinch.verification.sms.verification.interceptor.processor

import com.sinch.verification.sms.verification.extractor.SmsCodeExtractor
import com.sinch.verificationcore.internal.error.CodeInterceptionException
import com.sinch.verificationcore.internal.pattern.CodeExtractor
import com.sinch.verificationcore.verification.CodeExtractorListener

/**
 * [MessageProcessor] that creates proper instances of [SmsCodeExtractor] and uses it to notify the
 * listener about the sms verification code extraction process result.
 * @param codeExtractorListener Listener to be notified about the code extraction results.
 */
class SmsMessageProcessor(private val codeExtractorListener: CodeExtractorListener) :
    MessageProcessor {

    private var extractor: CodeExtractor? = null
    private var interceptedMessage: String? = null

    override fun onNewMessage(message: String) {
        interceptedMessage = message
        tryDataExtraction()
    }

    override fun onTemplateReceived(template: String) {
        try {
            extractor = SmsCodeExtractor(template)
        } catch (e: Exception) {
            codeExtractorListener.onCodeExtractionError(e)
        }
        tryDataExtraction()
    }

    private fun tryDataExtraction() {
        val currentMessage = interceptedMessage
        val currentExtractor = extractor

        if (currentExtractor != null && currentMessage != null) {
            currentExtractor.extract(currentMessage)?.let {
                codeExtractorListener.onCodeExtracted(it)
            } ?: codeExtractorListener.onCodeExtractionError(
                CodeInterceptionException("Failed to extract code from message")
            )

        }
    }
}