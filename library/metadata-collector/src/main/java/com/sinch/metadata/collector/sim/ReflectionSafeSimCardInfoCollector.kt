package com.sinch.metadata.collector.sim

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.telephony.SubscriptionManager
import androidx.annotation.RequiresApi
import com.sinch.metadata.collector.PermissionProtectedMetadataCollector
import com.sinch.metadata.model.sim.OperatorInfo
import com.sinch.metadata.model.sim.SimCardInfo
import com.sinch.verification.utils.api.ApiLevelUtils
import com.sinch.verification.utils.permission.Permission

/**
 * Metadata collector responsible for collecting array of metadata of type [SimCardInfo].
 * This implementation is used only on post Lollipop Android devices as it is possible to collect
 * desired data using official Android API on these API levels.
 * @param context Context reference.
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
class ReflectionSafeSimCardInfoCollector(context: Context) :
    PermissionProtectedMetadataCollector<List<SimCardInfo>?>(context, Permission.READ_PHONE_STATE) {

    private val subscriptionManager: SubscriptionManager by lazy {
        context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
    }

    override val metadataDescriptiveName: String = "Sim Card information"

    override fun collectWithPermissionsGranted(): List<SimCardInfo>? = collectOperatorSimCardData()

    @Suppress("DEPRECATION")
    @SuppressLint("MissingPermission")
    private fun collectOperatorSimCardData(): List<SimCardInfo> =
        subscriptionManager.activeSubscriptionInfoList.map {
            SimCardInfo(
                null, OperatorInfo(
                    it.countryIso,
                    it.carrierName.toString(),
                    subscriptionManager.isNetworkRoaming(it.subscriptionId),
                    if (ApiLevelUtils.isApi29OrLater) it.mccString else it.mcc.toString(),
                    if (ApiLevelUtils.isApi29OrLater) it.mncString else it.mnc.toString()
                )
            )
        }
}