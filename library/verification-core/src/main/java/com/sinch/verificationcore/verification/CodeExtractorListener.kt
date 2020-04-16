package com.sinch.verificationcore.verification

interface CodeExtractorListener {
    fun onCodeExtracted(code: String)
    fun onCodeExtractionError(e: Throwable)
}