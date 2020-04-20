@file:Suppress("DEPRECATION")

package com.sinch.metadata.model.network

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class NetworkType(val value: String) {
    @SerialName("MOBILE")
    MOBILE("MOBILE"),

    @SerialName("WIFI")
    WIFI("WIFI"),

    @SerialName("ETHERNET")
    ETHERNET("ETHERNET"),

    @SerialName("Not Connected")
    NONE("Not Connected");

    companion object {
        fun basedOn(capabilities: NetworkCapabilities): NetworkType = when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> WIFI
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> MOBILE
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> ETHERNET
            else -> NONE
        }

        fun basedOn(networkInfo: NetworkInfo): NetworkType = when (networkInfo.type) {
            ConnectivityManager.TYPE_WIFI -> WIFI
            ConnectivityManager.TYPE_MOBILE -> MOBILE
            ConnectivityManager.TYPE_ETHERNET -> ETHERNET
            else -> NONE
        }
    }
}