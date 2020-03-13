package com.sinch.verificationcore.request.metadata

import com.sinch.verificationcore.request.metadata.sim.SimCardData

data class PhoneMetadata(
    val version: Int,
    val deviceId: String,
    val os: String,
    val platform: String,
    val sdk: String,
    val buildFlavor: String,
    val simCardCount: Int,
    val simCardsInfo: List<SimCardData>
)