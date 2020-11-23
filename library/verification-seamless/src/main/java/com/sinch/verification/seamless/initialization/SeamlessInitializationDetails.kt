package com.sinch.verification.seamless.initialization

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Class containing details (returned by the API) about the initiated seamless verification process.
 * @property targetUri URI address at which the client has to make a GET call.
 */
@Serializable
data class SeamlessInitializationDetails(
    @SerialName("targetUri") val targetUri: String
)