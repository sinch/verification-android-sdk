package com.sinch.sinchverification.utils

import android.content.Context
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.FileNotFoundException

private const val CONFIG_ASSET_FILENAME = "config.json"

/** Example of json.config file
{
"appKey": "***",
"appSecret": "***",
"environment": "ocra.api.sinch.com"
}
 */
val Context.defaultConfigs: List<AppConfig>
    get() {
        try {
            return try {
                jsonAssetAsObject<Array<AppConfig>>(CONFIG_ASSET_FILENAME).toList()
            } catch (ignored: Exception) {
                listOf(jsonAssetAsObject(CONFIG_ASSET_FILENAME))
            }
        } catch (e: FileNotFoundException) {
            throw RuntimeException(
                "Config file not present. Put config.json file inside assets " +
                        "folder first then re-run the application."
            )
        }
    }

inline fun <reified T> Context.jsonAssetAsObject(jsonFileName: String): T =
    Json.Default.decodeFromString(
        JSONAssetsParser.inputToString(assets.open(jsonFileName))
    )