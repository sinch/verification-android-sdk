package com.sinch.verification.all.auto.verification.events

import com.sinch.verification.core.internal.VerificationMethodType
import com.sinch.verification.core.verification.VerificationEvent

/**
 * [VerificationEvent] that can be created by the automatic verification method.
 */
sealed class AutoVerificationEvent : VerificationEvent {

    /**
     * Event that is triggered whenever automatic verification initiates automatic code interception for given method.
     * @param method Method of the sub verification.
     */
    data class SubMethodAutomaticCodeInterceptionInitiatedEvent(val method: VerificationMethodType) :
        AutoVerificationEvent()

    /**
     * Event that is triggered whenever automatic verification tries to verify the number using given method.
     * @param method Method of the sub verification.
     */
    data class SubMethodVerificationCallEvent(val method: VerificationMethodType) :
        AutoVerificationEvent()

    /**
     * Event that is triggered whenever automatic verification method fails the sub verification process.
     * @param method Method of the sub verification.
     * @param e Error describing why sub verification has failed.
     */
    data class SubMethodFailedEvent(val method: VerificationMethodType, val e: Exception) :
        AutoVerificationEvent()
}
