package com.sinch.metadata

import android.content.Context
import com.sinch.metadata.collector.PermissionsCollector
import com.sinch.metadata.model.PermissionsMetadata
import com.sinch.utils.permission.Permission
import com.sinch.utils.permission.isPermissionGranted

/**
 * Collector responsible for collecting [PermissionsMetadata].
 * @param context Context reference.
 */
class BasicPermissionsCollector(private val context: Context) : PermissionsCollector {

    override fun collect(): PermissionsMetadata =
        PermissionsMetadata(
            hasReadPhoneStatePermission = context.isPermissionGranted(Permission.READ_PHONE_STATE),
            hasReadCallLogPermission = context.isPermissionGranted(Permission.READ_CALL_LOG),
            hasCallPhonePermission = context.isPermissionGranted(Permission.CALL_PHONE),
            hasReadSmsPermission = context.isPermissionGranted(Permission.READ_SMS),
            hasReceiveSmsPermission = context.isPermissionGranted(Permission.RECEIVE_SMS),
            hasAccessNetworkStatePermission = context.isPermissionGranted(Permission.ACCESS_NETWORK_STATE),
            hasCellularSignalPermission = context.isPermissionGranted(Permission.ACCESS_COARSE_LOCATION) || context.isPermissionGranted(
                Permission.ACCESS_FINE_LOCATION
            )
        )
}