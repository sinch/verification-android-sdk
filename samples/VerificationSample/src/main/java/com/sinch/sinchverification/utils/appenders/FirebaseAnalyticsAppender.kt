//package com.sinch.sinchverification.utils.appenders
//
//import com.google.firebase.analytics.FirebaseAnalytics
//import com.google.firebase.analytics.ktx.analytics
//import com.google.firebase.analytics.ktx.logEvent
//import com.google.firebase.ktx.Firebase
//import com.sinch.logging.Appender
//
//class FirebaseAnalyticsAppender : Appender {
//
//    companion object {
//        const val LOG_PARAM_NAME = "LOG"
//    }
//
//    private val firebaseAnalytics: FirebaseAnalytics by lazy {
//        Firebase.analytics
//    }
//
//    override fun trace(tag: String, msg: String, t: Throwable?) {
//        logParam(tag, msg, t)
//    }
//
//    override fun info(tag: String, msg: String, t: Throwable?) {
//        logParam(tag, msg, t)
//    }
//
//    override fun debug(tag: String, msg: String, t: Throwable?) {
//        logParam(tag, msg, t)
//    }
//
//    override fun warn(tag: String, msg: String, t: Throwable?) {
//        logParam(tag, msg, t)
//    }
//
//    override fun error(tag: String, msg: String, t: Throwable?) {
//        logParam(tag, msg, t)
//    }
//
//    private fun logParam(tag: String, msg: String, t: Throwable?) {
//        firebaseAnalytics.logEvent(LOG_PARAM_NAME) {
//            param("tag", tag)
//            param("msg", msg)
//            param("throwable", t?.message.orEmpty())
//        }
//    }
//}