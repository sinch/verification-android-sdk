package com.sinch.metadata.collector

import android.content.Context
import android.os.BatteryManager
import android.os.Build
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(
    RobolectricTestRunner::class
)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class BatteryLevelCollectorTests {

    @MockK
    lateinit var context: Context

    @MockK
    lateinit var batteryManager: BatteryManager

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        every { context.getSystemService(Context.BATTERY_SERVICE) } returns batteryManager
    }

    @Test
    fun testBatteryLevelFormat() {
        val testCollector = BasicBatteryLevelCollector(context)
        every { batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY) } returns 50
        assertEquals(testCollector.collect(), "50%")
    }

}