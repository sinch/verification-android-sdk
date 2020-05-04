package com.sinch.metadata.model.sim

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Class holding metadata about network operator the phone is connected to.
 * @property countryId ISO country code of currently connected network.
 * @property name Name of the operator.
 * @property isRoaming Flag indicating if network is in roaming mode.
 * @property mcc Mobile Country Code - See [wikipedia](https://en.wikipedia.org/wiki/Mobile_country_code)
 * @property mnc Mobile Network Code - See [wikipedia](https://en.wikipedia.org/wiki/Mobile_country_code)
 */
@Serializable
data class OperatorInfo(
    @SerialName("countryId") val countryId: String,
    @SerialName("name") val name: String,
    @SerialName("isRoaming") val isRoaming: Boolean,
    @SerialName("mcc") val mcc: String?,
    @SerialName("mnc") val mnc: String?
)