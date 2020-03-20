package com.sinch.metadata.model.sim

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SimCardInfo(
    @SerialName("sim") val simData: SimMetadata?,
    @SerialName("operator") val operatorData: OperatorInfo
)