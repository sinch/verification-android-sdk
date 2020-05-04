package com.sinch.verificationcore.verification

import com.sinch.verificationcore.internal.pattern.CodeExtractor

/**
 * Listener holding callbacks invoked by [CodeExtractor] notifying about the code extraction process result.
 */
interface CodeExtractorListener {

    /**
     * Called when the extractor has successfully extracted the verification code.
     */
    fun onCodeExtracted(code: String)

    /**
     * Called when the extractor has failed to extract the code.
     * @param e Error describing want went wrong.
     */
    fun onCodeExtractionError(e: Throwable)
}