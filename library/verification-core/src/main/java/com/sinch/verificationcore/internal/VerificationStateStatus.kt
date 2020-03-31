package com.sinch.verificationcore.internal

enum class VerificationStateStatus {
    ONGOING,
    SUCCESS,
    ERROR;

    val isFinished: Boolean get() = this != ONGOING
}