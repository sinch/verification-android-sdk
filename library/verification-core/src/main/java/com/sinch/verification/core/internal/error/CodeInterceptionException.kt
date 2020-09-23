package com.sinch.verification.core.internal.error

import com.sinch.verification.core.verification.interceptor.CodeInterceptor

/**
 * General exception that is thrown when error occurs during the process of automatic interception of verification codes.
 * @param message Human readable message of want went wrong during code interception.
 * @see CodeInterceptor
 */
open class CodeInterceptionException(message: String?) : Exception(message)