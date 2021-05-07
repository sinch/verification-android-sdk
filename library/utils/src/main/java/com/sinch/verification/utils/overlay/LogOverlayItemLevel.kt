package com.sinch.verification.utils.overlay

import android.content.Context
import androidx.core.content.ContextCompat
import com.sinch.verification.utils.R

enum class LogOverlayItemLevel {
    Trace,
    Info,
    Debug,
    Warn,
    Error
}

fun LogOverlayItemLevel.getColor(context: Context) = ContextCompat.getColor(
    context, when (this) {
        LogOverlayItemLevel.Trace -> R.color.log_level_trace
        LogOverlayItemLevel.Debug -> R.color.log_level_debug
        LogOverlayItemLevel.Error -> R.color.log_level_error
        LogOverlayItemLevel.Warn -> R.color.log_level_warn
        LogOverlayItemLevel.Info -> R.color.log_level_info
    }
)