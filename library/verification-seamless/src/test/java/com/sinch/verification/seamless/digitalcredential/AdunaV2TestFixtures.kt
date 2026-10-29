package com.sinch.verification.seamless.digitalcredential

internal object AdunaV2TestFixtures {

    const val NONCE = "d4a56cc72ee04c51805e6b981ce9362b"
    const val CREDENTIAL_ID = "test-credential-id"
    const val FORMAT = "jwt_vc"
    const val CREDENTIAL_AUTHORIZATION_JWT =
        "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.test-jwt-payload"

    val VP_RESPONSE_JSON = """
        {
          "id": "$CREDENTIAL_ID",
          "format": "$FORMAT",
          "claims": [
            {"path": ["phone_number_hint"], "values": ["+48678567345"]},
            {"path": ["carrier_hint"], "values": ["carrier-001"]}
          ],
          "meta": {
            "credential_authorization_jwt": "$CREDENTIAL_AUTHORIZATION_JWT",
            "vct_values": ["vct-value-1"]
          }
        }
    """.trimIndent()
}
