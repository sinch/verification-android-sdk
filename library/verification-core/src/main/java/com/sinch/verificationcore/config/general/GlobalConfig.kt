package com.sinch.verificationcore.config.general

import android.content.Context
import retrofit2.Retrofit

interface GlobalConfig {
    val context: Context
    val retrofit: Retrofit
}