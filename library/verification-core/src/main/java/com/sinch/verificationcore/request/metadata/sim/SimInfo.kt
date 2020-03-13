package com.sinch.verificationcore.request.metadata.sim

data class SimInfo(
    val countryId: String,
    val mcc: String,
    val mnc: String
)