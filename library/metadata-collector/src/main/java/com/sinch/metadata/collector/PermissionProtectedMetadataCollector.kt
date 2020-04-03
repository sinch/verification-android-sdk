package com.sinch.metadata.collector

import android.content.Context
import com.sinch.logging.logger
import com.sinch.utils.permission.Permission
import com.sinch.utils.permission.PermissionUtils
import com.sinch.utils.permission.isPermissionGranted
import com.sinch.utils.permission.runIfPermissionGranted

abstract class PermissionProtectedMetadataCollector<Metadata>(
    protected val context: Context,
    private val permission: Permission
) : MetadataCollector<Metadata?> {

    protected val logger = logger()

    abstract val metadataDescriptiveName: String

    final override fun collect(): Metadata? {
        if (!context.isPermissionGranted(permission)) {
            logger.warn(
                PermissionUtils.permissionMetadataWarning(
                    permission,
                    metadataDescriptiveName
                )
            )
        }
        return context.runIfPermissionGranted(permission, this::collectWithPermissionsGranted)
    }

    abstract fun collectWithPermissionsGranted(): Metadata?

}