package com.sinch.sinchverification.utils.appenders

import com.sinch.logging.Appender
import com.sinch.sinchverification.utils.logoverlay.LogOverlay

class LogOverlayAppender : Appender {

    override fun trace(tag: String, msg: String, t: Throwable?) {
        logWithoutLevel(tag, msg)
    }

    override fun info(tag: String, msg: String, t: Throwable?) {
        logWithoutLevel(tag, msg)
    }

    override fun debug(tag: String, msg: String, t: Throwable?) {
        logWithoutLevel(tag, msg)
    }

    override fun warn(tag: String, msg: String, t: Throwable?) {
        logWithoutLevel(tag, msg)
    }

    override fun error(tag: String, msg: String, t: Throwable?) {
        logWithoutLevel(tag, msg)
    }

    private fun logWithoutLevel(tag: String, msg: String) {
        LogOverlay.log(tag, msg)
    }

}