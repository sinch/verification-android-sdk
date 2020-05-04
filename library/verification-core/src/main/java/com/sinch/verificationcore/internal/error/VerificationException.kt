package com.sinch.verificationcore.internal.error

/**
 * General exception used for notifications about errors during the verification process.
 * @param message Human readable message of want went wrong during the verification process.
 */
class VerificationException(override val message: String) : Exception(message)