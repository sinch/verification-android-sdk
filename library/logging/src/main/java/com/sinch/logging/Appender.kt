package com.sinch.logging

interface Appender {
    fun trace(tag: String, msg: String, t: Throwable? = null)
    fun info(tag: String, msg: String, t: Throwable? = null)
    fun debug(tag: String, msg: String, t: Throwable? = null)
    fun warn(tag: String, msg: String, t: Throwable? = null)
    fun error(tag: String, msg: String, t: Throwable? = null)
}
