package com.sinch.sinchverification.utils

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class AppConfig(
    val name: String,
    val appKey: String,
    val environment: String,
    @Transient val isCustom: Boolean = false
) {
    companion object {
        const val CUSTOM_CONFIG_NAME = "Custom"
    }
}
