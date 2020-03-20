package com.sinch.metadata.collector

import android.app.Application
import android.content.Context
import android.os.Build
import android.telephony.SubscriptionInfo
import android.telephony.SubscriptionManager
import androidx.test.core.app.ApplicationProvider
import com.sinch.metadata.collector.sim.ReflectionSafeSimCardInfoCollector
import com.sinch.metadata.model.sim.OperatorInfo
import com.sinch.metadata.model.sim.SimCardInfo
import com.sinch.utils.permission.Permission
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowSubscriptionManager

@RunWith(
    RobolectricTestRunner::class
)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class SimCardInfoCollectorTests {

    companion object {
        const val C1_ISO = "pl"
        const val C1_OPERATOR = "Orange"

        const val C2_ISO = "ang"
        const val C2_OPERATOR = "T-Mobile"
    }

    private val context = ApplicationProvider.getApplicationContext<Application>()
    private val shadowApplication = Shadows.shadowOf(context)
    private val subscriptionManager =
        Shadows.shadowOf(context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager)
    private val shadowSimCardCollector = ReflectionSafeSimCardInfoCollector(context)

    private val testInfo1: SubscriptionInfo
        get() = ShadowSubscriptionManager.SubscriptionInfoBuilder.newBuilder()
            .setCountryIso(C1_ISO)
            .setCarrierName(C1_OPERATOR)
            .buildSubscriptionInfo()

    private val testInfo2: SubscriptionInfo
        get() = ShadowSubscriptionManager.SubscriptionInfoBuilder.newBuilder()
            .setCountryIso(C2_ISO)
            .setCarrierName(C2_OPERATOR)
            .buildSubscriptionInfo()

    @Before
    fun setUp() {
        shadowApplication.grantPermissions(Permission.READ_PHONE_STATE.androidValue)
    }

    @Test
    fun testNullIfNoPermissionsGranted() {
        shadowApplication.denyPermissions(Permission.READ_PHONE_STATE.androidValue)
        Assert.assertNull(shadowSimCardCollector.collect())
    }

    @Test
    fun testSingleSubscription() {
        subscriptionManager.setActiveSubscriptionInfos(testInfo1)
        val collectedData = shadowSimCardCollector.collect()
        Assert.assertEquals(
            collectedData,
            listOf(SimCardInfo(null, OperatorInfo(C1_ISO, C1_OPERATOR, false, "0", "0")))
        )
    }

    @Test
    fun testMultipleSubscriptions() {
        subscriptionManager.setActiveSubscriptionInfos(testInfo1, testInfo2)
        val collectedData = shadowSimCardCollector.collect()
        Assert.assertNotNull(collectedData)
        Assert.assertEquals(
            collectedData,
            listOf(
                SimCardInfo(null, OperatorInfo(C1_ISO, C1_OPERATOR, false, "0", "0")),
                SimCardInfo(null, OperatorInfo(C2_ISO, C2_OPERATOR, false, "0", "0"))
            )
        )
    }
}