package com.sinch.metadata

import android.content.Context
import com.sinch.metadata.collector.BasicBatteryLevelCollector
import com.sinch.metadata.collector.BasicDeviceMetadataCollector
import com.sinch.metadata.collector.BasicLocaleCollector
import com.sinch.metadata.collector.BasicNetworkInfoCollector
import com.sinch.metadata.collector.sim.BasicSimStateCollector
import com.sinch.metadata.collector.sim.LollipopSimCardInfoCollector
import com.sinch.metadata.collector.sim.ReflectionSafeSimCardInfoCollector
import com.sinch.metadata.model.PhoneMetadata
import com.sinch.metadata.model.PhoneMetadataFactory
import com.sinch.utils.api.ApiLevelUtils

/**
 * Factory responsible for creating entire [PhoneMetadata] on Android devices.
 * @param context Context reference
 * @param sdk Version of the library.
 * @param sdkFlavor Product flavor used to build the library.
 */
class AndroidMetadataFactory(
    private val context: Context, private val sdk: String,
    private val sdkFlavor: String
) : PhoneMetadataFactory {

    override fun create(): PhoneMetadata {
        return PhoneMetadata.createUsing(
            sdk,
            sdkFlavor,
            BasicDeviceMetadataCollector(),
            if (ApiLevelUtils.isApi22OrLater) ReflectionSafeSimCardInfoCollector(context) else LollipopSimCardInfoCollector(),
            BasicSimStateCollector(context),
            BasicLocaleCollector(context),
            BasicPermissionsCollector(context),
            BasicNetworkInfoCollector(context),
            BasicBatteryLevelCollector(context)
        )
    }
}