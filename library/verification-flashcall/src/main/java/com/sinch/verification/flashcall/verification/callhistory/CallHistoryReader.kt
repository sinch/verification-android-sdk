package com.sinch.verification.flashcall.verification.callhistory

interface CallHistoryReader {
    fun readIncomingCalls(sinceEpoch: Long): List<String>
}