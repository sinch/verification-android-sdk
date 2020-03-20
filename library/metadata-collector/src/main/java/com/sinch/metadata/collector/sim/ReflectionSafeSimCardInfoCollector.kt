package com.sinch.metadata.collector.sim

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.telephony.SubscriptionManager
import androidx.annotation.RequiresApi
import com.sinch.metadata.collector.SimCardInfoCollector
import com.sinch.metadata.model.sim.OperatorInfo
import com.sinch.metadata.model.sim.SimCardInfo
import com.sinch.utils.api.ApiLevelUtils
import com.sinch.utils.permission.Permission
import com.sinch.utils.permission.runIfPermissionGranted

@RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
class ReflectionSafeSimCardInfoCollector(private val context: Context) :
    SimCardInfoCollector {

    private val subscriptionManager: SubscriptionManager by lazy {
        context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
    }

    override fun collect(): List<SimCardInfo>? {
        return context.runIfPermissionGranted(
            Permission.READ_PHONE_STATE,
            this::collectOperatorSimCardData
        )
    }

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