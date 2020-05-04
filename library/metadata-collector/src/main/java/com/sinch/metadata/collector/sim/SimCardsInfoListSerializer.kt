package com.sinch.metadata.collector.sim

import com.sinch.metadata.model.sim.SimCardInfo
import kotlinx.serialization.builtins.list
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonTransformingSerializer
import kotlinx.serialization.json.json

const val TRANSFORMATION_NAME = "SimCardsInfoListTransform"

/**
 * Serializer used for mapping [SimCardInfo] lists passed in requests containing phone metadata fields as
 * sinch api uses not standard JSON for processing these properties.
 *
 * Example JSON:
 * ```
 * {
 *    "1":{
 *       "sim":null,
 *       "operator":{
 *          "countryId":"pl",
 *          "name":"Orange",
 *          "isRoaming":false,
 *          "mcc":"260",
 *          "mnc":"3"
 *       }
 *    },
 *    "count":1
 * }
 * ```
 */
object SimCardsInfoListSerializer :
    JsonTransformingSerializer<List<SimCardInfo>>(
        SimCardInfo.serializer().list,
        TRANSFORMATION_NAME
    ) {

    private const val COUNT_FIELD_NAME = "count"

    override fun writeTransform(element: JsonElement): JsonElement {
        return when {
            element !is JsonArray -> throw IllegalStateException("Only lists can be transformed")
            element.size == 0 -> json {
                COUNT_FIELD_NAME to 0
            }
            else -> json {
                for (i in 0 until element.size) {
                    "${i + 1}" to element[i]
                }
                COUNT_FIELD_NAME to element.size
            }
        }
    }
}