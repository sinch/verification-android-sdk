package com.sinch.metadata.collector

import android.os.Build
import com.sinch.metadata.model.DeviceMetadata

class BasicDeviceMetadataCollector : DeviceMetadataCollector {

    override fun collect(): DeviceMetadata =
        DeviceMetadata(Build.MODEL, Build.DEVICE, Build.MANUFACTURER)

}