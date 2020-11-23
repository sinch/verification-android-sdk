@file:Suppress("unused")

package com.sinch.verification.utils.permission

import android.content.Context
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Delegated property that can be read only if the specified permission was granted (null otherwise).
 * @property context Context reference.
 * @property permission Permission that needs to be granted to read the property.
 * @property f Code black that should return desired property (assuming permission was granted).
 */
class PermissionProtectedProperty<T>(
    private val context: Context,
    private val permission: Permission,
    private val f: () -> T?
) :
    ReadOnlyProperty<Any?, T?> {

    override fun getValue(thisRef: Any?, property: KProperty<*>): T? =
        context.runIfPermissionGranted(permission, f)

}