package com.sinch.verificationcore.internal

import com.google.gson.annotations.SerializedName

enum class VerificationMethodType(val value: String) {
    @SerializedName("sms")
    SMS("sms")
}