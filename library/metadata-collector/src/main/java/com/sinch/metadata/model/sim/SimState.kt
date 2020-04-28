package com.sinch.metadata.model.sim

import android.telephony.TelephonyManager
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class SimState(val value: String) {
    @SerialName("SIM_STATE_UNKNOWN")
    UNKNOWN("SIM_STATE_UNKNOWN"),

    @SerialName("SIM_STATE_ABSENT")
    ABSENT("SIM_STATE_ABSENT"),

    @SerialName("SIM_STATE_PIN_REQUIRED")
    PIN_REQUIRED("SIM_STATE_PIN_REQUIRED"),

    @SerialName("SIM_STATE_PUK_REQUIRED")
    PUK_REQUIRED("SIM_STATE_PUK_REQUIRED"),

    @SerialName("SIM_STATE_NETWORK_LOCKED")
    NETWORK_LOCKED("SIM_STATE_NETWORK_LOCKED"),

    @SerialName("SIM_STATE_READY")
    READY("SIM_STATE_READY"),

    @SerialName("SIM_STATE_INVALID")
    INVALID("SIM_STATE_INVALID");


    companion object {

        fun basedOn(telephonySimState: Int): SimState = when (telephonySimState) {
            TelephonyManager.SIM_STATE_UNKNOWN -> UNKNOWN
            TelephonyManager.SIM_STATE_ABSENT -> ABSENT
            TelephonyManager.SIM_STATE_PIN_REQUIRED -> PIN_REQUIRED
            TelephonyManager.SIM_STATE_PUK_REQUIRED -> PUK_REQUIRED
            TelephonyManager.SIM_STATE_NETWORK_LOCKED -> NETWORK_LOCKED
            TelephonyManager.SIM_STATE_READY -> READY
            else -> INVALID
        }

        @Suppress("unused")
        @JvmStatic
        fun forKey(value: String) = values().first { it.value == value }
    }
}