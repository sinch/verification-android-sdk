package com.sinch.metadata.model.sim

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Class holding metadata about currently used sim card and operator of network the phone
 * is connected to. To better understand the differences between properties of these fields
 * check [this](https://stackoverflow.com/questions/38726068/android-mcc-and-mnc) SO question.
 * @property simData Object holding information about sim card.
 * @property operatorData Object holding information about current network operator.
 */
@Serializable
data class SimCardInfo(
    @SerialName("sim") val simData: SimMetadata?,
    @SerialName("operator") val operatorData: OperatorInfo
)