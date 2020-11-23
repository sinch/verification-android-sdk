package com.sinch.verification.sms.verification.interceptor

import com.sinch.verification.core.internal.error.CodeInterceptionException

/**
 * Exception used by [SmsBroadcastReceiver] to inform the [SmsBroadcastListener] about an error.
 */
class SmsReceiverException(message: String?) : CodeInterceptionException(message)