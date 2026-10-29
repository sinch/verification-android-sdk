package com.sinch.verification.seamless.digitalcredential

import android.os.Build
import androidx.annotation.RequiresApi
import com.sinch.logging.logger
import java.util.Base64
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

@RequiresApi(Build.VERSION_CODES.O)
internal class DigitalCredentialMapper(private val json: Json) {

    private val logger = logger()

    fun buildRequestJson(vpResponseJson: String, nonce: String?): String {
        val vpResponse = json.decodeFromString<VpResponse>(vpResponseJson)
        val request = DigitalCredentialRequest(
            requests = listOf(
                ProtocolRequest(
                    protocol = "openid4vp-v1-unsigned",
                    data = RequestData(
                        dcqlQuery = DcqlQuery(
                            credentials = listOf(
                                CmCredentials(
                                    claims = vpResponse.claims,
                                    format = vpResponse.format,
                                    id = vpResponse.id,
                                    meta = vpResponse.meta
                                )
                            )
                        ),
                        nonce = nonce,
                        responseMode = "dc_api",
                        responseType = "vp_token"
                    )
                )
            )
        )
        return json.encodeToString(request)
    }

    private fun extractNonceFromJwt(vpResponse: VpResponse): String? {
        val jwt = (vpResponse.meta as? JsonObject)
            ?.get("credential_authorization_jwt")
            ?.jsonPrimitive?.content
            ?: return null
        return try {
            val payloadB64 = jwt.split(".").getOrNull(1) ?: return null
            val decoded = Base64.getUrlDecoder().decode(payloadB64)
            json.decodeFromString<JsonObject>(decoded.decodeToString())["nonce"]
                ?.jsonPrimitive?.content
        } catch (e: Exception) {
            logger.error("Failed to extract nonce from JWT: ${e.message}")
            null
        }
    }
}
