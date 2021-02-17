package com.sinch.sinchverification.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.sinch.sinchverification.Environment

class SharedPrefsManager(context: Context) {

    companion object {
        private const val CUSTOM_URL_KEY = "CUSTOM_URL"
    }

    private val preferences = defaultPrefs(context)

    fun appKey(environment: Environment): String {
        return preferences[environment.name]
    }

    fun setAppKey(environment: Environment, appKey: String) {
        preferences[environment.name] = appKey
    }

    var customURL: String
        get() = preferences[CUSTOM_URL_KEY]
        set(value) {
            preferences[CUSTOM_URL_KEY] = value
        }

    private fun defaultPrefs(context: Context): SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    private fun customPrefs(context: Context, name: String): SharedPreferences =
        context.getSharedPreferences(name, Context.MODE_PRIVATE)

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = this.edit()
        operation(editor)
        editor.apply()
    }

    operator fun SharedPreferences.set(key: String, value: Any?) = when (value) {
        is String? -> edit { it.putString(key, value) }
        is Int -> edit { it.putInt(key, value) }
        is Boolean -> edit { it.putBoolean(key, value) }
        is Float -> edit { it.putFloat(key, value) }
        is Long -> edit { it.putLong(key, value) }
        else -> throw UnsupportedOperationException("Not yet implemented")
    }

    inline operator fun <reified T : Any> SharedPreferences.get(
        key: String,
        defaultValue: T? = null
    ): T = when (T::class) {
        String::class -> getString(key, defaultValue as? String ?: "") as T
        Int::class -> getInt(key, defaultValue as? Int ?: -1) as T
        Boolean::class -> getBoolean(key, defaultValue as? Boolean ?: false) as T
        Float::class -> getFloat(key, defaultValue as? Float ?: -1f) as T
        Long::class -> getLong(key, defaultValue as? Long ?: -1) as T
        else -> throw UnsupportedOperationException("Not yet implemented")
    }
}