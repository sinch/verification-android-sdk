package com.sinch.verificationcore.internal.error.earlyreject

class EarlyRejectException(val data: EarlyRejectData) : Exception("Early Reject ${data.message}")