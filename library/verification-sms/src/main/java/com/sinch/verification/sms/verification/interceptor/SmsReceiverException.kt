package com.sinch.verification.sms.verification.interceptor

import com.sinch.verificationcore.internal.error.CodeInterceptionException

/**
 * Exception used by [SmsBroadcastReceiver] to inform the [SmsBroadcastListener] about an error.
 */
class SmsReceiverException(message: String?) : CodeInterceptionException(message)