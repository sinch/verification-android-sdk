package com.sinch.metadata.collector.sim

import com.sinch.metadata.collector.SimCardInfoCollector
import com.sinch.metadata.model.sim.SimCardInfo

/**
 * Metadata collector responsible for collecting array of metadata of type [SimCardInfo].
 * This implementation is used only on Lollipop Android devices and simply returns null as it
 * is not possible to extract desired metadata without using reflection on that API level.
 */
class LollipopSimCardInfoCollector : SimCardInfoCollector {

    override fun collect(): List<SimCardInfo>? {
        return null
    }

}