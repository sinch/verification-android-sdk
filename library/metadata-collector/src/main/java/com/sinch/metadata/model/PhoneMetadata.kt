package com.sinch.metadata.model

import android.os.Build
import com.sinch.metadata.collector.*
import com.sinch.metadata.collector.sim.SimCardsInfoListSerializer
import com.sinch.metadata.model.network.NetworkInfo
import com.sinch.metadata.model.sim.SimCardInfo
import com.sinch.metadata.model.sim.SimState
import com.sinch.utils.Factory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

typealias PhoneMetadataFactory = Factory<PhoneMetadata>

@Serializable
data class PhoneMetadata(
    @SerialName("os") val os: String = Build.VERSION.RELEASE,
    @SerialName("platform") val platform: String = "Android",
    @SerialName("sdk") val sdk: String,
    @SerialName("buildFlavor") val buildFlavor: String,
    @SerialName("device") val device: DeviceMetadata,
    @SerialName("simCardsInfo") @Serializable(SimCardsInfoListSerializer::class) val simCardsInfo: List<SimCardInfo>?,
    @SerialName("simState") val simState: SimState,
    @SerialName("defaultLocale") val defaultLocale: String,
    @SerialName("permissions") val permissionsMetadata: PermissionsMetadata,
    @SerialName("networkInfo") val networkInfo: NetworkInfo,
    @SerialName("batteryLevel") val batteryLevel: String
) {
    companion object {
        const val METADATA_VERSION_NUMBER = 2

        fun createUsing(
            sdk: String,
            sdkFlavor: String,
            deviceMetadataCollector: DeviceMetadataCollector,
            simCardInfoCollector: SimCardInfoCollector,
            simsStateCollector: SimsStateCollector,
            localeCollector: LocaleCollector,
            permissionsCollector: PermissionsCollector,
            networkInfoCollector: NetworkInfoCollector,
            batteryLevelCollector: BatteryLevelCollector
        ) =
            PhoneMetadata(
                os = Build.VERSION.RELEASE,
                sdk = sdk,
                buildFlavor = sdkFlavor,
                device = deviceMetadataCollector.collect(),
                simCardsInfo = simCardInfoCollector.collect(),
                simState = simsStateCollector.collect(),
                defaultLocale = localeCollector.collect(),
                permissionsMetadata = permissionsCollector.collect(),
                networkInfo = networkInfoCollector.collect(),
                batteryLevel = batteryLevelCollector.collect()
            )
    }
    @SerialName("version")
    private val version = METADATA_VERSION_NUMBER
    @SerialName("simCardCount")
    private val simCardCount: Int? = simCardsInfo?.size

}