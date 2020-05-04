package com.sinch.verification.flashcall.verification.callhistory

import android.content.ContentResolver
import android.net.Uri
import android.provider.CallLog

/**
 * [CallHistoryReader] that uses [ContentResolver] API to read incoming phone calls from
 * Android call log.
 * @param contentResolver Content resolver reference.
 */
class ContentProviderCallHistoryReader(private val contentResolver: ContentResolver) :
    CallHistoryReader {

    companion object {
        val callUri: Uri = CallLog.Calls.CONTENT_URI
    }

    private val columns = arrayOf(CallLog.Calls.DATE, CallLog.Calls.NUMBER, CallLog.Calls.TYPE)

    override fun readIncomingCalls(sinceEpoch: Long): List<String> {
        val foundCalls = mutableListOf<String>()
        val cursor = contentResolver.query(
            callUri,
            columns,
            whereClause(sinceEpoch),
            null,
            CallLog.Calls.DATE
        )
        cursor?.run {
            val numberIndex = cursor.getColumnIndex(CallLog.Calls.NUMBER)
            val typeIndex = cursor.getColumnIndex(CallLog.Calls.TYPE)
            while (cursor.moveToNext()) {
                val callType = cursor.getInt(typeIndex)
                if (callType == CallLog.Calls.INCOMING_TYPE || callType == CallLog.Calls.MISSED_TYPE) {
                    foundCalls.add(cursor.getString(numberIndex))
                }
            }
            cursor.close()
        }
        return foundCalls
    }

    private fun whereClause(sinceEpoch: Long): String = "${CallLog.Calls.DATE} > $sinceEpoch"
}