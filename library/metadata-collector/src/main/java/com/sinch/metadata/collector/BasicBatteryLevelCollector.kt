package com.sinch.metadata.collector

import android.content.Context
import android.os.BatteryManager

class BasicBatteryLevelCollector(private val context: Context) : BatteryLevelCollector {

    private val batteryManager: BatteryManager by lazy {
        context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
    }

    override fun collect(): String {
        val capacity = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        return "$capacity%"
    }
}