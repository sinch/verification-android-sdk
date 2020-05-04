package com.sinch.metadata.model.sim

import android.telephony.TelephonyManager
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Enum representing possible Android Framework sim state values.
 */
@Serializable
enum class SimState(val value: String) {

    /**
     * Unknown sim state.
     * @see TelephonyManager.SIM_STATE_UNKNOWN
     */
    @SerialName("SIM_STATE_UNKNOWN")
    UNKNOWN("SIM_STATE_UNKNOWN"),

    /**
     * Absent sim state.
     * @see TelephonyManager.SIM_STATE_ABSENT
     */
    @SerialName("SIM_STATE_ABSENT")
    ABSENT("SIM_STATE_ABSENT"),

    /**
     * Pin required sim state.
     * @see TelephonyManager.SIM_STATE_PIN_REQUIRED
     */
    @SerialName("SIM_STATE_PIN_REQUIRED")
    PIN_REQUIRED("SIM_STATE_PIN_REQUIRED"),

    /**
     * Puk required sim state.
     * @see TelephonyManager.SIM_STATE_PUK_REQUIRED
     */
    @SerialName("SIM_STATE_PUK_REQUIRED")
    PUK_REQUIRED("SIM_STATE_PUK_REQUIRED"),

    /**
     * Network locked sim state.
     * @see TelephonyManager.SIM_STATE_NETWORK_LOCKED
     */
    @SerialName("SIM_STATE_NETWORK_LOCKED")
    NETWORK_LOCKED("SIM_STATE_NETWORK_LOCKED"),

    /**
     * Sim state ready.
     * @see TelephonyManager.SIM_STATE_READY
     */
    @SerialName("SIM_STATE_READY")
    READY("SIM_STATE_READY"),

    /**
     * Invalid sim state (no representation in Android framework).
     */
    @SerialName("SIM_STATE_INVALID")
    INVALID("SIM_STATE_INVALID");


    companion object {

        /**
         * Creates [SimState] based on Android framework sim state value.
         * @param telephonySimState Integer sim state representation taken from [TelephonyManager].
         * @return Sinch representation of SimState
         */
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