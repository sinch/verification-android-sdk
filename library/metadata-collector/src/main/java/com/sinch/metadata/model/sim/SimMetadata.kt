package com.sinch.metadata.model.sim

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Class holding metadata about currently used sim card.
 * @property countryId ISO country code of the sim card.
 * @property mcc Mobile Country Code - See [wikipedia](https://en.wikipedia.org/wiki/Mobile_country_code)
 * @property mnc Mobile Network Code - See [wikipedia](https://en.wikipedia.org/wiki/Mobile_country_code)
 */
@Serializable
data class SimMetadata(
    @SerialName("countryId") val countryId: String,
    @SerialName("mcc") val mcc: String,
    @SerialName("mnc") val mnc: String
)