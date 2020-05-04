package com.sinch.verificationcore.verification.interceptor

import com.sinch.verificationcore.internal.error.CodeInterceptionException

/**
 * Exception that is passed to [CodeInterceptionListener] when the code has not been intercepted after [CodeInterceptor.maxTimeout] has passed.
 */
class CodeInterceptionTimeoutException : CodeInterceptionException("Interception timed out")