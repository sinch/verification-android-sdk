package com.sinch.verificationcore.verification.interceptor

import com.sinch.verificationcore.internal.error.CodeInterceptionException

class CodeInterceptionTimeoutException : CodeInterceptionException("Interception timed out")