@file:Suppress("unused")

package com.sinch.utils.permission

import android.content.Context
import androidx.core.content.PermissionChecker
import androidx.core.content.PermissionChecker.checkSelfPermission

const val PermissionsUtilsMockkName = "com.sinch.utils.permission.PermissionUtilsKt"

object PermissionUtils {

    fun isPermissionGranted(context: Context, permission: Permission): Boolean {
        return checkSelfPermission(
            context,
            permission.androidValue
        ) == PermissionChecker.PERMISSION_GRANTED
    }

    fun permissionMetadataWarning(missingPermission: Permission, metadataName: String) =
        "Missing $missingPermission to collect metadata: $metadataName"

}

fun Context.isPermissionGranted(permission: Permission): Boolean {
    return PermissionUtils.isPermissionGranted(
        this,
        permission
    )
}

fun <T> Context.runIfPermissionGranted(
    permission: Permission,
    grantedBlock: () -> T?,
    notGrantedBlock: () -> Unit = {}
): T? =
    if (isPermissionGranted(permission)) {
        grantedBlock()
    } else {
        notGrantedBlock()
        null
    }