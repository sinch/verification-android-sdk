package com.sinch.metadata.collector

import android.content.Context
import com.sinch.logging.logger
import com.sinch.utils.permission.Permission
import com.sinch.utils.permission.PermissionUtils
import com.sinch.utils.permission.isPermissionGranted
import com.sinch.utils.permission.runIfPermissionGranted

/**
 * Convenient metadata collector implementations that grabs the metadata only if specified
 * permission was granted.
 * @param Metadata Specific Metadata class.
 * @property context Context Reference.
 * @property permission Permission that needs to be granted to collect the metadata.t
 */
@Suppress("MemberVisibilityCanBePrivate")
abstract class PermissionProtectedMetadataCollector<Metadata>(
    protected val context: Context,
    private val permission: Permission
) : MetadataCollector<Metadata?> {

    protected val logger = logger()

    /**
     * Descriptive metadata name (to be used as metadata name in logger messages).
     */
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

    /**
     * Collects the metadata with assumption that the permission was granted (no need implement any
     * permission checks).
     * @return Desired metadata if permission was granted, null otherwise.
     */
    abstract fun collectWithPermissionsGranted(): Metadata?

}