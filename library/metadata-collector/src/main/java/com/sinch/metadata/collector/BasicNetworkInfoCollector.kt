package com.sinch.metadata.collector

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.telephony.TelephonyManager
import com.sinch.metadata.model.network.NetworkData
import com.sinch.metadata.model.network.NetworkInfo
import com.sinch.metadata.model.network.NetworkType
import com.sinch.utils.api.ApiLevelUtils
import com.sinch.utils.permission.Permission
import com.sinch.utils.permission.runIfPermissionGranted

class BasicNetworkInfoCollector(private val context: Context) : NetworkInfoCollector {

    private val telephonyManager: TelephonyManager by lazy {
        context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    }
    private val connectivityManager: ConnectivityManager by lazy {
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    override fun collect(): NetworkInfo {
        val isVoiceCapable = telephonyManager.isVoiceCapableSafe
        val networkData =
            context.runIfPermissionGranted(
                Permission.ACCESS_NETWORK_STATE,
                this::collectNetworkData
            )
        return NetworkInfo(isVoiceCapable, networkData)
    }

    private fun collectNetworkData(): NetworkData {
        return NetworkData(connectivityManager.activeNetworkType)
    }

    private val TelephonyManager.isVoiceCapableSafe: Boolean?
        get() =
            if (ApiLevelUtils.isApi22OrLater) {
                isVoiceCapable
            } else {
                null
            }

    private val ConnectivityManager.activeNetworkType: NetworkType
        @SuppressLint("MissingPermission")
        get() =
            if (ApiLevelUtils.isApi23OrLater) {
                getNetworkCapabilities(activeNetwork)?.let { NetworkType.basedOn(it) }
                    ?: NetworkType.NONE
            } else {
                @Suppress("DEPRECATION")
                activeNetworkInfo?.let { NetworkType.basedOn(it) } ?: NetworkType.NONE
            }

}