package com.sinch.utils.permission

import android.content.Context
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class PermissionProtectedProperty<T>(
    private val context: Context,
    private val permission: Permission,
    private val f: () -> T?
) :
    ReadOnlyProperty<Any?, T?> {

    override fun getValue(thisRef: Any?, property: KProperty<*>): T? =
        context.runIfPermissionGranted(permission, f)

}