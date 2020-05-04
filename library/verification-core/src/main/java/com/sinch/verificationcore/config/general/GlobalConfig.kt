package com.sinch.verificationcore.config.general

import android.content.Context
import retrofit2.Retrofit

/**
 * Interface defining requirements of global SDK configuration.
 */
interface GlobalConfig {

    /**
     * Application context reference. DO NOT use activity as context here as it may outlive global config instances.
     */
    val context: Context

    /**
     * Retrofit instance used for communication with Sinch API.
     */
    val retrofit: Retrofit
}