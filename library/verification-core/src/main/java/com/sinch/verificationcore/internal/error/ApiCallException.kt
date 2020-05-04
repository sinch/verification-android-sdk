package com.sinch.verificationcore.internal.error

/**
 * Exception representing errors returned by Sinch verification API.
 * @param data Detailed data containing more information about what went wrong.
 */
class ApiCallException(val data: ApiErrorData) : Exception(data.message)