package com.sinch.verification.core.verification.interceptor

import com.sinch.verification.core.internal.error.CodeInterceptionException

/**
 * Exception that is passed to [CodeInterceptionListener] when the code has not been intercepted after [CodeInterceptor.interceptionTimeout] has passed.
 */
class CodeInterceptionTimeoutException : CodeInterceptionException("Interception timed out")