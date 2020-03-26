package com.sinch.smsverification.verification.interceptor

import com.sinch.verificationcore.internal.error.CodeInterceptionException

class SmsReceiverException(message: String?) : CodeInterceptionException(message)