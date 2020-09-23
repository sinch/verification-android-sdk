package com.sinch.verification.sms.verification.interceptor.processor

import com.sinch.verification.core.internal.pattern.CodeExtractor
import com.sinch.verification.core.verification.interceptor.BasicCodeInterceptor

/**
 * General interface used to create classes that handles communication between [CodeExtractor] and [BasicCodeInterceptor]
 */
interface MessageProcessor {

    /**
     * Callback invoked whenever new sms is received and code extraction should be started.
     */
    fun onNewMessage(message: String)

    /**
     * Callback invoked whenever new template is received and new [CodeExtractor] should be created.
     */
    fun onTemplateReceived(template: String)
}