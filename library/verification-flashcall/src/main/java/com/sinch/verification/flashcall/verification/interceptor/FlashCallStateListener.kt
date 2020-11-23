package com.sinch.verification.flashcall.verification.interceptor

import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager

/**
 * Observer used to notify it's [IncomingCallListener] about incoming verification phone call.
 */
class FlashCallStateListener(private val listener: IncomingCallListener) : PhoneStateListener() {

    override fun onCallStateChanged(state: Int, phoneNumber: String?) {
        super.onCallStateChanged(state, phoneNumber)
        if (state == TelephonyManager.CALL_STATE_RINGING && !phoneNumber.isNullOrEmpty()) {
            listener.onIncomingCallRinging(phoneNumber)
        }
    }

}