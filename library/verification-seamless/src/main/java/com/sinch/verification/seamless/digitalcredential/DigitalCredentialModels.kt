package com.sinch.verification.seamless.digitalcredential

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
internal data class VpResponse(
    @SerialName("claims") val claims: List<JsonElement> = emptyList(),
    @SerialName("format") val format: String? = null,
    @SerialName("id") val id: String? = null,
    @SerialName("meta") val meta: JsonElement? = null
)

@Serializable
internal data class DigitalCredentialRequest(
    val requests: List<ProtocolRequest> = listOf()
)

@Serializable
internal data class ProtocolRequest(
    val protocol: String = "",
    val data: RequestData = RequestData()
)

@Serializable
internal data class RequestData(
    @SerialName("dcql_query") val dcqlQuery: DcqlQuery = DcqlQuery(),
    val nonce: String? = null,
    @SerialName("response_mode") val responseMode: String = "",
    @SerialName("response_type") val responseType: String = ""
)

@Serializable
internal data class DcqlQuery(
    val credentials: List<CmCredentials> = emptyList()
)

@Serializable
internal data class CmCredentials(
    val claims: List<JsonElement> = emptyList(),
    val format: String? = null,
    val id: String? = null,
    val meta: JsonElement? = null
)
