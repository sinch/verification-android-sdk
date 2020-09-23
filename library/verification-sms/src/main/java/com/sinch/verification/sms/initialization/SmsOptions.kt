package com.sinch.verification.sms.initialization

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Class containing initiation details for sms verification.
 * @property applicationHash Application hash used to automatically intercept the message. [See](https://developers.sinch.com/docs/verification-android-the-verification-process#automatic-code-extraction-from-sms)
 */
@Serializable
data class SmsOptions(@SerialName("applicationHash") val applicationHash: String? = null)