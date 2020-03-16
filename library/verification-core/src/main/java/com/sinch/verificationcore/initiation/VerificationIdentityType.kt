package com.sinch.verificationcore.initiation

import com.google.gson.annotations.SerializedName

enum class VerificationIdentityType(val value: String) {
    @SerializedName("number")
    NUMBER("number")
}