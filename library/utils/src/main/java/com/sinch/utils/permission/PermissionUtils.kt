@file:Suppress("unused")

package com.sinch.utils.permission

import android.content.Context
import androidx.core.content.PermissionChecker
import androidx.core.content.PermissionChecker.checkSelfPermission

const val PermissionsUtilsMockkName = "com.sinch.utils.permission.PermissionUtilsKt"

/**
 * Object containing utility functions connected with permissions.
 */
object PermissionUtils {

    /**
     * Checks if user has granted specific permission.
     * @param context Context reference
     * @param permission Permission that is checked
     * @return Boolean indicating if specific permission was granted
     */
    fun isPermissionGranted(context: Context, permission: Permission): Boolean {
        return checkSelfPermission(
            context,
            permission.androidValue
        ) == PermissionChecker.PERMISSION_GRANTED
    }

    /**
     * Creates logger message describing that the permission required to collect desired metadata
     * was missing.
     * @param missingPermission Permission that is missing to collect the metadata
     * @param metadataName name of the metadata field
     * @return String with the warning.
     */
    fun permissionMetadataWarning(missingPermission: Permission, metadataName: String) =
        "Missing $missingPermission to collect metadata: $metadataName"

}

/**
 * Convenient version of [PermissionUtils.isPermissionGranted] with context as the receiver.
 * @receiver Context reference
 * @param permission Permission that is checked
 * @see PermissionUtils.isPermissionGranted
 */
fun Context.isPermissionGranted(permission: Permission): Boolean {
    return PermissionUtils.isPermissionGranted(
        this,
        permission
    )
}

/**
 * Runs specified code blocks depending whether the permission was granted or not
 * @receiver Context reference
 * @param permission Permission that is checked
 * @param grantedBlock Code block that is run if permission is granted
 * @param notGrantedBlock Code block that is run if permission is not granted
 */
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