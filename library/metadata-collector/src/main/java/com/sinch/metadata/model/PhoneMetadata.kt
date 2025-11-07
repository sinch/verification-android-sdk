package com.sinch.metadata.model

import android.os.Build
import com.sinch.metadata.collector.BatteryLevelCollector
import com.sinch.metadata.collector.DeviceMetadataCollector
import com.sinch.metadata.collector.LocaleCollector
import com.sinch.metadata.collector.MetadataCollector
import com.sinch.metadata.collector.NetworkInfoCollector
import com.sinch.metadata.collector.PermissionsCollector
import com.sinch.metadata.collector.SimCardInfoCollector
import com.sinch.metadata.collector.SimsStateCollector
import com.sinch.metadata.collector.sim.SimCardsInfoListSerializer
import com.sinch.metadata.model.network.NetworkInfo
import com.sinch.metadata.model.sim.SimCardInfo
import com.sinch.metadata.model.sim.SimState
import com.sinch.verification.utils.Factory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

typealias PhoneMetadataFactory = Factory<PhoneMetadata>

/**
 * Container class for all the metadata collected by the SDK.
 * @property os User visibility version of the operating system (Android).
 * @property platform System the library is installed on (for Android SDK this value is always "Android")
 * @property sdk Version of the sdk.
 * @property buildFlavor Build flavor used to build the sdk.
 * @property device General device metadata.
 * @property simCardsInfo List containing metadata about installed sim cards.
 * @property simState Current state of installed sim card.
 * @property defaultLocale Locale of the device.
 * @property permissionsMetadata Metadata containing information about permissions user has granted to the application.
 * @property networkInfo Metadata containing information about data network the phone is connected to.
 * @property batteryLevel Current battery level.
 */
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

        /**
         * Version of JSON that is sent to the API.
         */
        const val METADATA_VERSION_NUMBER = 2

        /**
         * Creates metadata object using specified collectors and properties.
         * @param sdk Version of the sdk
         * @param sdkFlavor Build flavor used to build the sdk.
         * @param deviceMetadataCollector Collector used to get device metadata.
         * @param simCardInfoCollector Collector used to get sim card info metadata.
         * @param simsStateCollector Collector used to get sim state metadata.
         * @param localeCollector Collector used to get locale metadata.
         * @param permissionsCollector Collector used to get permissions metadata.
         * @param networkInfoCollector Collector used to get data network metadata.
         * @param batteryLevelCollector Collector used to get current battery level metadata.
         * @see MetadataCollector
         */
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