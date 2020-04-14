package com.sinch.verification.flashcall.verification.callhistory

import android.content.ContentResolver
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.provider.CallLog

class SinchCallHistoryChangeObserver(private val callHistoryChangeListener: CallHistoryChangeListener) :
    ContentObserver(Handler()) {

    override fun onChange(selfChange: Boolean, uri: Uri?) {
        super.onChange(selfChange, uri)
        callHistoryChangeListener.onCallHistoryChanged()
    }

    fun registerOn(contentResolver: ContentResolver) {
        contentResolver.registerContentObserver(CallLog.Calls.CONTENT_URI, true, this)
    }

    fun unregisterFrom(contentResolver: ContentResolver) {
        contentResolver.unregisterContentObserver(this)
    }

}