package com.sinch.sinchverification.utils.appenders

import android.util.Log
import com.bugfender.sdk.Bugfender
import com.sinch.logging.Appender

class BugfenderAppender : Appender {

    override fun trace(tag: String, msg: String, t: Throwable?) {
        Bugfender.t(tag, msg.withThrowable(t))
    }

    override fun info(tag: String, msg: String, t: Throwable?) {
        Bugfender.i(tag, msg.withThrowable(t))
    }

    override fun debug(tag: String, msg: String, t: Throwable?) {
        Bugfender.d(tag, msg.withThrowable(t))
    }

    override fun warn(tag: String, msg: String, t: Throwable?) {
        Bugfender.w(tag, msg.withThrowable(t))
    }

    override fun error(tag: String, msg: String, t: Throwable?) {
        Bugfender.e(tag, msg.withThrowable(t))
    }

    private fun String.withThrowable(t: Throwable?): String =
        if (t == null) this else "$this\n${Log.getStackTraceString(t)}"
}
