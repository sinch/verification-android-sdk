package com.sinch.metadata.collector.sim

import com.sinch.metadata.model.sim.SimCardInfo
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.*

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
        ListSerializer(SimCardInfo.serializer())
    ) {

    private const val COUNT_FIELD_NAME = "count"

    override fun transformSerialize(element: JsonElement): JsonElement {
        return when {
            element !is JsonArray -> throw IllegalStateException("Only lists can be transformed")
            element.size == 0 -> buildJsonObject {
                put(COUNT_FIELD_NAME, 0)
            }

            else -> buildJsonObject {
                for (i in 0 until element.size) {
                    put("${i + 1}", element[i])
                }
                put(COUNT_FIELD_NAME, element.size)
            }
        }
    }

}