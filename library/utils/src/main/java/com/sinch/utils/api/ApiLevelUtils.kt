package com.sinch.utils.api

import android.os.Build

object ApiLevelUtils {
    val isApi22OrLater: Boolean get() = Build.VERSION.SDK_INT >= 22
    val isApi23OrLater: Boolean get() = Build.VERSION.SDK_INT >= 23
    val isApi29OrLater: Boolean get() = Build.VERSION.SDK_INT >= 29
    val isApi24OrLater: Boolean get() = Build.VERSION.SDK_INT >= 24
}