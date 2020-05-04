package com.sinch.verification.flashcall.verification.callhistory

/**
 * Interface defining callbacks for accessing call logs.
 */
interface CallHistoryReader {

    /**
     * Reads incoming call log.
     * @param sinceEpoch Epoch value since which the call history should be read.
     * @return List of incoming phone calls received after [sinceEpoch].
     */
    fun readIncomingCalls(sinceEpoch: Long): List<String>

}