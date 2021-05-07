package com.sinch.sinchverification.utils.appenders

import com.sinch.logging.Appender
import com.sinch.verification.utils.overlay.LogOverlay
import com.sinch.verification.utils.overlay.LogOverlayItemLevel

class LogOverlayAppender : Appender {

    override fun trace(tag: String, msg: String, t: Throwable?) {
        LogOverlay.log(tag, msg, LogOverlayItemLevel.Trace)
    }

    override fun info(tag: String, msg: String, t: Throwable?) {
        LogOverlay.log(tag, msg, LogOverlayItemLevel.Info)
    }

    override fun debug(tag: String, msg: String, t: Throwable?) {
        LogOverlay.log(tag, msg, LogOverlayItemLevel.Debug)
    }

    override fun warn(tag: String, msg: String, t: Throwable?) {
        LogOverlay.log(tag, msg, LogOverlayItemLevel.Warn)
    }

    override fun error(tag: String, msg: String, t: Throwable?) {
        LogOverlay.log(tag, msg, LogOverlayItemLevel.Error)
    }

}