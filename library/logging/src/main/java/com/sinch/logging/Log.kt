package com.sinch.logging

object Log {

    private val delegates = mutableSetOf<Appender>()

    @JvmStatic
    @JvmOverloads
    fun <T : Any> create(type: T, tagOverride: String? = null): Logger = DelegatingLogger(
        tagOverride
            ?: type::class.java.simpleName
    )

    @JvmStatic
    fun init(vararg appenders: Appender) {
        with(delegates) {
            clear()
            addAll(appenders)
        }
    }

    private class DelegatingLogger(override val tag: String) : Logger {
        override fun trace(msg: String, t: Throwable?) {
            delegates.forEach {
                it.trace(tag, msg, t)
            }
        }

        override fun info(msg: String, t: Throwable?) {
            delegates.forEach {
                it.info(tag, msg, t)
            }
        }

        override fun debug(msg: String, t: Throwable?) {
            delegates.forEach {
                it.debug(tag, msg, t)
            }
        }

        override fun warn(msg: String, t: Throwable?) {
            delegates.forEach {
                it.warn(tag, msg, t)
            }
        }

        override fun error(msg: String, t: Throwable?) {
            delegates.forEach {
                it.error(tag, msg, t)
            }
        }
    }
}

fun Any.logger(): Logger = Log.create(this)

fun Any.logger(tag: String): Logger = Log.create(this, tag)