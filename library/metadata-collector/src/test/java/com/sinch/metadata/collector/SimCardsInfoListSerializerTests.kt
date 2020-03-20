package com.sinch.metadata.collector

import com.sinch.metadata.collector.sim.SimCardsInfoListSerializer
import com.sinch.metadata.model.sim.OperatorInfo
import com.sinch.metadata.model.sim.SimCardInfo
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.json.JsonElement
import org.junit.Assert.*
import org.junit.Test

class SimCardsInfoListSerializerTests {

    companion object {
        private const val COUNT_FIELD_NAME = "count"
        private const val FIELD_NAME_1 = "1"
        private const val FIELD_NAME_2 = "2"
    }

    private val simpleData1: SimCardInfo
        get() = SimCardInfo(
            null,
            OperatorInfo(
                countryId = "pl",
                name = "Orange",
                isRoaming = false,
                mcc = "260",
                mnc = "3"
            )
        )

    private val simpleData2: SimCardInfo
        get() = SimCardInfo(
            null,
            OperatorInfo(
                countryId = "ang",
                name = "T-Mobile",
                isRoaming = true,
                mcc = "233",
                mnc = "2"
            )
        )

    private val json = Json(JsonConfiguration.Stable)

    @Test
    fun testSimpleSingleCardInfo() {
        val jsonData = json.toJson(SimCardsInfoListSerializer, listOf(simpleData1))
        assertTrue(jsonData.contains(FIELD_NAME_1))
        assertTrue(jsonData.contains(COUNT_FIELD_NAME))

        val firstObject = jsonData.jsonObject.getObject(FIELD_NAME_1)
        assertEquals(firstObject, serializeSimInfo(simpleData1))
        assertEquals(jsonData.countValue, 1)
    }

    @Test
    fun test2CardInfo() {
        val jsonData = json.toJson(SimCardsInfoListSerializer, listOf(simpleData1, simpleData2))

        assertTrue(jsonData.contains(COUNT_FIELD_NAME))
        assertTrue(jsonData.contains(FIELD_NAME_1))
        assertTrue(jsonData.contains(FIELD_NAME_2))

        val firstObject = jsonData.jsonObject.getObject(FIELD_NAME_1)
        val secondObject = jsonData.jsonObject.getObject(FIELD_NAME_2)

        assertEquals(firstObject, serializeSimInfo(simpleData1))
        assertEquals(secondObject, serializeSimInfo(simpleData2))
        assertEquals(jsonData.countValue, 2)
    }

    @Test
    fun testEmptyCardInfo() {
        val jsonData = json.toJson(SimCardsInfoListSerializer, emptyList())
        assertTrue(jsonData.contains(COUNT_FIELD_NAME))
        assertFalse(jsonData.contains(FIELD_NAME_1))
        assertEquals(jsonData.jsonObject.keys.size, 1)
        assertEquals(jsonData.countValue, 0)
    }

    private val JsonElement.countValue: Int get() = jsonObject.getPrimitive(COUNT_FIELD_NAME).int

    private fun serializeSimInfo(simCardInfo: SimCardInfo) = json.toJson(
        SimCardInfo.serializer(),
        simCardInfo
    )
}