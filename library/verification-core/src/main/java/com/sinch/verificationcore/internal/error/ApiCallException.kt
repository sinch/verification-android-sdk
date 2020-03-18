package com.sinch.verificationcore.internal.error

class ApiCallException(val data: ApiErrorData) : Exception(data.message)