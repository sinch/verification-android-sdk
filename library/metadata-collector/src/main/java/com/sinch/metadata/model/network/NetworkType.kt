@file:Suppress("DEPRECATION")

package com.sinch.metadata.model.network

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Enum representing type of network (data connection) the phone might be connected to.
 */
@Serializable
enum class NetworkType(val value: String) {

    /**
     * Mobile provider data connection case.
     */
    @SerialName("MOBILE")
    MOBILE("MOBILE"),

    /**
     * WiFi data connection case.
     */
    @SerialName("WIFI")
    WIFI("WIFI"),

    /**
     * Ethernet data connection case.
     */
    @SerialName("ETHERNET")
    ETHERNET("ETHERNET"),

    /**
     * No data connection.
     */
    @SerialName("Not Connected")
    NONE("Not Connected");

    companion object {

        /**
         * Returns [NetworkType] based on [NetworkCapabilities] of tested network. This method
         * can be used only on API levels above 22.
         * @param capabilities capabilities of checked network.
         * @return Type of checked network.
         */
        fun basedOn(capabilities: NetworkCapabilities): NetworkType = when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> WIFI
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> MOBILE
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> ETHERNET
            else -> NONE
        }

        /**
         * Returns [NetworkType] based on [NetworkInfo] of tested network. This method should be
         * used on API levels 22 and below where [NetworkCapabilities] are not supported.
         * @param networkInfo Network info object of checked network.
         * @return Type of checked network.
         */
        fun basedOn(networkInfo: NetworkInfo): NetworkType = when (networkInfo.type) {
            ConnectivityManager.TYPE_WIFI -> WIFI
            ConnectivityManager.TYPE_MOBILE -> MOBILE
            ConnectivityManager.TYPE_ETHERNET -> ETHERNET
            else -> NONE
        }
    }
}