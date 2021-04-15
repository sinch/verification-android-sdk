package com.sinch.logging.appenders

import android.util.Log
import com.sinch.logging.Appender

class LogcatAppender : Appender {

    override fun trace(tag: String, msg: String, t: Throwable?) {}

    override fun info(tag: String, msg: String, t: Throwable?) {
        Log.i(tag, msg, t)
    }

    override fun debug(tag: String, msg: String, t: Throwable?) {
        Log.d(tag, msg, t)
    }

    override fun warn(tag: String, msg: String, t: Throwable?) {
        Log.w(tag, msg, t)
    }

    override fun error(tag: String, msg: String, t: Throwable?) {
        Log.e(tag, msg, t)
    }

}