package com.sinch.verification.seamless.digitalcredential

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class DigitalCredentialMapperTest {

    companion object {
        private const val NONCE = "abc"
    }

    private val json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

    private lateinit var mapper: DigitalCredentialMapper

    @Before
    fun setUp() {
        mapper = DigitalCredentialMapper(json)
    }

    @Test
    fun `buildRequestJson produces single protocol request`() {
        val result = json.decodeFromString<JsonObject>(
            mapper.buildRequestJson(AdunaV2TestFixtures.VP_RESPONSE_JSON, NONCE)
        )
        assertEquals(1, result["requests"]!!.jsonArray.size)
    }

    @Test
    fun `buildRequestJson sets openid4vp protocol`() {
        val result = json.decodeFromString<JsonObject>(
            mapper.buildRequestJson(AdunaV2TestFixtures.VP_RESPONSE_JSON, NONCE)
        )
        val protocol = result["requests"]!!.jsonArray[0].jsonObject["protocol"]!!.jsonPrimitive.content
        assertEquals("openid4vp-v1-unsigned", protocol)
    }

    @Test
    fun `buildRequestJson sets correct response_mode and response_type`() {
        val result = json.decodeFromString<JsonObject>(
            mapper.buildRequestJson(AdunaV2TestFixtures.VP_RESPONSE_JSON, NONCE)
        )
        val data = result["requests"]!!.jsonArray[0].jsonObject["data"]!!.jsonObject
        assertEquals("dc_api", data["response_mode"]!!.jsonPrimitive.content)
        assertEquals("vp_token", data["response_type"]!!.jsonPrimitive.content)
    }

    @Test
    fun `buildRequestJson maps claims from vpResponse into dcql credentials`() {
        val result = json.decodeFromString<JsonObject>(
            mapper.buildRequestJson(AdunaV2TestFixtures.VP_RESPONSE_JSON, NONCE)
        )
        val credential = dcqlCredential(result)
        assertEquals(2, credential["claims"]!!.jsonArray.size)
        val firstPath = credential["claims"]!!.jsonArray[0].jsonObject["path"]!!.jsonArray[0].jsonPrimitive.content
        assertEquals("phone_number_hint", firstPath)
    }

    @Test
    fun `buildRequestJson maps format and id from vpResponse into dcql credentials`() {
        val result = json.decodeFromString<JsonObject>(
            mapper.buildRequestJson(AdunaV2TestFixtures.VP_RESPONSE_JSON, NONCE)
        )
        val credential = dcqlCredential(result)
        assertEquals(AdunaV2TestFixtures.FORMAT, credential["format"]!!.jsonPrimitive.content)
        assertEquals(AdunaV2TestFixtures.CREDENTIAL_ID, credential["id"]!!.jsonPrimitive.content)
    }

    @Test
    fun `buildRequestJson maps meta from vpResponse into dcql credentials`() {
        val result = json.decodeFromString<JsonObject>(
            mapper.buildRequestJson(AdunaV2TestFixtures.VP_RESPONSE_JSON, NONCE)
        )
        val meta = dcqlCredential(result)["meta"]!!.jsonObject
        assertEquals(
            AdunaV2TestFixtures.CREDENTIAL_AUTHORIZATION_JWT,
            meta["credential_authorization_jwt"]!!.jsonPrimitive.content
        )
        assertEquals("vct-value-1", meta["vct_values"]!!.jsonArray[0].jsonPrimitive.content)
    }

    @Test
    fun `buildRequestJson includes provided nonce`() {
        val result = json.decodeFromString<JsonObject>(
            mapper.buildRequestJson(AdunaV2TestFixtures.VP_RESPONSE_JSON, NONCE)
        )
        val nonce = result["requests"]!!.jsonArray[0]
            .jsonObject["data"]!!.jsonObject["nonce"]!!.jsonPrimitive.content
        assertEquals(NONCE, nonce)
    }

    @Test
    fun `buildRequestJson yields null nonce when none provided`() {
        val result = json.decodeFromString<JsonObject>(
            mapper.buildRequestJson(AdunaV2TestFixtures.VP_RESPONSE_JSON, null)
        )
        val nonce = result["requests"]!!.jsonArray[0]
            .jsonObject["data"]!!.jsonObject["nonce"]!!.jsonPrimitive.contentOrNull
        assertNull(nonce)
    }

    private fun dcqlCredential(result: JsonObject): JsonObject =
        result["requests"]!!.jsonArray[0]
            .jsonObject["data"]!!.jsonObject["dcql_query"]!!
            .jsonObject["credentials"]!!.jsonArray[0].jsonObject
}
