package com.sinch.verification.utils

import android.net.ConnectivityManager
import android.net.ConnectivityManager.setProcessDefaultNetwork
import android.net.Network
import com.sinch.verification.utils.api.ApiLevelUtils
import java.util.concurrent.TimeUnit

val Long.Companion.MAX_TIMEOUT: Long get() = MAX_VALUE / 2 //for some reason if we use Long.MAX_VALUE on delayed Handler it simply runs instantly

fun TimeUnit.toMillisOrNull(duration: Long?) =
    duration?.let { this.toMillis(it) }

fun String.prefixed(prefix: String): String? = "$prefix$this"

fun ConnectivityManager.changeProcessNetworkTo(network: Network?): Boolean {
    return if (ApiLevelUtils.isApi23OrLater) {
        bindProcessToNetwork(network)
    } else {
        @Suppress("DEPRECATION") //Suppress annotation is required here as Android Studio doesn't process if/else statement correctly.
        setProcessDefaultNetwork(network)
    }
}