package com.sinch.utils

import android.app.Application
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.sinch.utils.permission.Permission
import com.sinch.utils.permission.PermissionUtils
import com.sinch.utils.permission.runIfPermissionGranted
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config

@RunWith(
    RobolectricTestRunner::class
)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class PermissionUtilsTest {

    private val context = ApplicationProvider.getApplicationContext<Application>()
    private val shadowApplication = Shadows.shadowOf(context)
    private val examplePermission get() = Permission.READ_SMS

    @Test
    fun testPermissionIsGranted() {
        shadowApplication.grantPermissions(examplePermission.androidValue)
        assertTrue(PermissionUtils.isPermissionGranted(context, examplePermission))
    }

    @Test
    fun testPermissionNotGranted() {
        shadowApplication.denyPermissions(examplePermission.androidValue)
        assertFalse(PermissionUtils.isPermissionGranted(context, examplePermission))
    }

    @Test
    fun testBlockNotExecuted() {
        shadowApplication.denyPermissions(examplePermission.androidValue)
        val returned = context.runIfPermissionGranted(examplePermission) {
            fail("Block should not be executed")
        }
        assertNull(returned)
    }

    @Test
    fun testBlockExecuted() {
        var wasBlockExecuted = false
        shadowApplication.grantPermissions(examplePermission.androidValue)
        context.runIfPermissionGranted(examplePermission) {
            wasBlockExecuted = true
        }
        assertTrue(wasBlockExecuted)
    }
}
