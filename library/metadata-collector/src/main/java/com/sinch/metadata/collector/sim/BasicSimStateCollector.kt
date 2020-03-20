package com.sinch.metadata.collector.sim

import android.content.Context
import android.telephony.TelephonyManager
import com.sinch.metadata.collector.SimsStateCollector
import com.sinch.metadata.model.sim.SimState

class BasicSimStateCollector(private val context: Context) :
    SimsStateCollector {

    private val telephonyManager: TelephonyManager by lazy {
        context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    }

    override fun collect(): SimState = SimState.basedOn(telephonyManager.simState)

}