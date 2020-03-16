package com.sinch.verificationcore.initiation.metadata.sim

data class OperatorInfo(
    val countryId: String,
    val name: String,
    val roaming: Boolean,
    val mcc: String,
    val mnc: String
)