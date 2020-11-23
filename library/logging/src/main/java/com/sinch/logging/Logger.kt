package com.sinch.logging

interface Logger {
    val tag: String
    fun trace(msg: String, t: Throwable? = null)
    fun info(msg: String, t: Throwable? = null)
    fun debug(msg: String, t: Throwable? = null)
    fun warn(msg: String, t: Throwable? = null)
    fun error(msg: String, t: Throwable? = null)
}
