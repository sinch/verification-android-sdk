package com.sinch.verificationcore.config

import android.content.Context
import retrofit2.Retrofit

interface Config {
    val context: Context
    val retrofit: Retrofit
}