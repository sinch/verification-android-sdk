package com.sinch.metadata.model.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Class holding metadata describing current network the phone is connected to.
 */
@Serializable
data class NetworkInfo(
    /**
     * Flag indicating if current network supports phone calls.
     * @see [TelephonyManager#isVoiceCapable](https://developer.android.com/reference/android/telephony/TelephonyManager#isVoiceCapable())
     */
    @SerialName("isVoiceCapable") val isVoiceCapable: Boolean?,

    /**
     * More detailed data about currently connected network.
     */
    @SerialName("data") val networkData: NetworkData?
)