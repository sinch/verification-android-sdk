package com.sinch.logging.appenders

import android.content.Context
import com.sinch.logging.Appender
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

class FileAppender(private val appContext: Context) : Appender {

    companion object {
        const val LOG_FILE_NAME = "applogs"
    }

    private enum class LogLevel(val fileString: String) {
        Trace("Trace"),
        Info("Info"),
        Debug("Debug"),
        Warn("WARN"),
        Error("---ERROR---")
    }

    private val logFileDir = appContext.getExternalFilesDir(null)
    private val dateFormatter = SimpleDateFormat.getDateTimeInstance()

    private val logFile: File? by lazy {
        val file = File(logFileDir, LOG_FILE_NAME)
        if (file.exists()) {
            file
        } else {
            file.takeIf { it.createNewFile() }
        }
    }

    override fun trace(tag: String, msg: String, t: Throwable?) {
        appendWithLevel(LogLevel.Trace, tag, msg, t)
    }

    override fun info(tag: String, msg: String, t: Throwable?) {
        appendWithLevel(LogLevel.Info, tag, msg, t)
    }

    override fun debug(tag: String, msg: String, t: Throwable?) {
        appendWithLevel(LogLevel.Debug, tag, msg, t)
    }

    override fun warn(tag: String, msg: String, t: Throwable?) {
        appendWithLevel(LogLevel.Warn, tag, msg, t)
    }

    override fun error(tag: String, msg: String, t: Throwable?) {
        appendWithLevel(LogLevel.Error, tag, msg, t)
    }

    private fun appendWithLevel(logLevel: LogLevel, tag: String, msg: String, t: Throwable?) {
        val fileToLog = logFile ?: return
        try {
            BufferedWriter(FileWriter(fileToLog, true)).use { writer ->
                val datePart = dateFormatter.format(Date()).centeredAtWidth(30)
                val levelPart = logLevel.fileString.centeredAtWidth(12)
                val tagPart = tag.centeredAtWidth(30)
                writer.append("|$datePart| --- |$levelPart| --- |$tagPart| --- | $msg")
                writer.newLine()
                t?.let {
                    writer.append(t.stackTraceToString())
                    writer.newLine()
                }
            }
        } catch (e: Exception) {

        }
    }

    private fun String.centeredAtWidth(width: Int): String =
        String.format(
            format = "%-${width}s",
            String.format(
                format = "%${length + (width - length) / 2}s",
                this
            )
        )
}