package com.sinch.sinchverification.utils

import com.sinch.logging.logger
import okhttp3.logging.HttpLoggingInterceptor

class HttpFileLogger : HttpLoggingInterceptor.Logger {

    private val logger = logger("HTTP-LOGS")

    override fun log(message: String) {
        logger.trace(message)
    }

}