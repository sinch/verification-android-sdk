package com.sinch.metadata.collector

import com.sinch.metadata.model.DeviceMetadata
import com.sinch.metadata.model.PermissionsMetadata
import com.sinch.metadata.model.network.NetworkInfo
import com.sinch.metadata.model.sim.SimCardInfo
import com.sinch.metadata.model.sim.SimState

typealias DeviceMetadataCollector = MetadataCollector<DeviceMetadata>
typealias SimCardInfoCollector = MetadataCollector<List<SimCardInfo>?>
typealias SimsStateCollector = MetadataCollector<SimState>
typealias LocaleCollector = MetadataCollector<String>
typealias BatteryLevelCollector = MetadataCollector<String>
typealias PermissionsCollector = MetadataCollector<PermissionsMetadata>
typealias NetworkInfoCollector = MetadataCollector<NetworkInfo>

/**
 * Base interface for collecting phone metadata used by Sinch API mostly for analytics and early
 * rejection rules.
 * @param Metadata Specific Metadata class.
 */
interface MetadataCollector<Metadata> {

    /**
     * Collects the phone metadata of type [Metadata].
     */
    fun collect(): Metadata

}