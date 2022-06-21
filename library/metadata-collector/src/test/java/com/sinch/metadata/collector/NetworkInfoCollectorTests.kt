package com.sinch.metadata.collector

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.sinch.metadata.model.network.NetworkType
import com.sinch.verification.utils.permission.Permission
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config

@RunWith(
    RobolectricTestRunner::class
)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class NetworkInfoCollectorTests {

    private val context = ApplicationProvider.getApplicationContext<Application>()
    private val shadowApplication = Shadows.shadowOf(context)
    private val shadowConnectivityManager =
        Shadows.shadowOf(context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)

    private val shadowCollector = BasicNetworkInfoCollector(context)

    @Test
    fun testDataIsNullWhenPermissionsDenied() {
        shadowApplication.denyPermissions(Permission.ACCESS_NETWORK_STATE.androidValue)
        val data = shadowCollector.collect().networkData
        Assert.assertNull(data)
    }

    @Test
    fun testDataIsPresentWithPermissions() {
        shadowApplication.grantPermissions(Permission.ACCESS_NETWORK_STATE.androidValue)
        val data = shadowCollector.collect().networkData
        Assert.assertNotNull(data)
    }

    @Ignore("Minimum support bumped from 21 -> 23")
    @Test
    @Config(sdk = [Build.VERSION_CODES.LOLLIPOP])
    fun testVoiceCapableNullOnApi21() {
        val networkInfo = shadowCollector.collect()
        Assert.assertNull(networkInfo.isVoiceCapable)
    }

    @Test
    fun testEmptyNetworkInfo() {
        shadowApplication.grantPermissions(Permission.ACCESS_NETWORK_STATE.androidValue)
        shadowConnectivityManager.clearAllNetworks()
        Assert.assertEquals(shadowCollector.collect().networkData?.type, NetworkType.NONE)
    }
}
