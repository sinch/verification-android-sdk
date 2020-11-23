package com.sinch.verification.flashcall.verification.interceptor

/**
 * Interface defining callback to be invoked when SDK detects incoming phone call.
 */
interface IncomingCallListener {

    /**
     * Incoming phone call callback.
     * @param number Full phone number of the incoming call.
     */
    fun onIncomingCallRinging(number: String)

}