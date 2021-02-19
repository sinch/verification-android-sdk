package com.sinch.sinchverification

import com.sinch.logging.Appender
import org.greenrobot.eventbus.EventBus

sealed class LogMessageEvent(open val tag: String, open val msg: String, open val t: Throwable?) {
    data class Info(
        override val tag: String,
        override val msg: String,
        override val t: Throwable?
    ) : LogMessageEvent(tag, msg, t)

    data class Debug(
        override val tag: String,
        override val msg: String,
        override val t: Throwable?
    ) : LogMessageEvent(tag, msg, t)
}

class EventBusAppender : Appender {

    override fun trace(tag: String, msg: String, t: Throwable?) {}

    override fun info(tag: String, msg: String, t: Throwable?) {
        EventBus.getDefault().post(LogMessageEvent.Info(tag, msg, t))
    }

    override fun debug(tag: String, msg: String, t: Throwable?) {
        EventBus.getDefault().post(LogMessageEvent.Debug(tag, msg, t))
    }

    override fun warn(tag: String, msg: String, t: Throwable?) {}

    override fun error(tag: String, msg: String, t: Throwable?) {}
}