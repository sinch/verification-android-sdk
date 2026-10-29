package com.sinch.verification.seamless.initialization

import com.sinch.verification.core.initiation.response.InitiationDetails
import com.sinch.verification.core.internal.VerificationMethodType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * Class containing details (returned by the API) about the initiated seamless verification process.
 * @property targetUri URI address at which the client has to make a GET call (v1 path).
 * @property version Protocol version returned by the API (e.g. "v2").
 * @property nonce Nonce value used in the v2 digital credential request.
 * @property vpResponse Raw JSON string of the VP response object, available on v2 responses.
 * @property androidAppUrl If present, the v2 flow redirects to this URL (carrier app) instead of using the Credential Manager.
 * @property appInfoJwt JWT carrying app/session info, appended to [androidAppUrl] as a query parameter.
 * @property appInfoJwtQueryParameterName Name of the query parameter used to pass [appInfoJwt] in the URL.
 * @property appCallbackQueryParameterName Name of the query parameter in the App Link callback URL that carries the operator token.
 * @property subVerificationId Id assigned to each verification method that can be used in case of [VerificationMethodType.AUTO]
 */
@Serializable
data class SeamlessInitializationDetails(
    @SerialName("targetUri") val targetUri: String? = null,
    @SerialName("subVerificationId") override val subVerificationId: String? = null,
    @SerialName("version") val version: String? = null,
    @SerialName("nonce") val nonce: String? = null,
    @SerialName("androidAppUrl") val androidAppUrl: String? = null,
    @SerialName("appInfoJwt") val appInfoJwt: String? = null,
    @SerialName("appInfoJwtQueryParameterName") val appInfoJwtQueryParameterName: String? = null,
    @SerialName("appCallbackQueryParameterName") val appCallbackQueryParameterName: String? = null,
    @SerialName("vpResponse") private val vpResponseElement: JsonElement? = null
) : InitiationDetails {
    /** The [vpResponse] object from the API serialized back to a JSON string, or null if absent. */
    val vpResponse: String? get() = vpResponseElement?.toString()

    val isV2: Boolean get() = version == VERSION_V2

    companion object {
        const val VERSION_V2 = "v2"
    }
}