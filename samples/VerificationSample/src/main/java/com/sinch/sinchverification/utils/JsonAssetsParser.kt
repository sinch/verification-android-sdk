package com.sinch.sinchverification.utils

import java.io.IOException
import java.io.InputStream

class JSONAssetsParser {

    companion object {
        fun inputToString(inputStream: InputStream): String {
            return try {
                val rawBytes = ByteArray(inputStream.available())
                inputStream.read(rawBytes, 0, rawBytes.size)
                String(rawBytes)
            } catch (e: IOException) {
                ""
            }
        }
    }

}