package com.sinch.sinchverification.utils

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

class SharedPrefsManager(private val appContext: Application) {

    companion object {
        private const val PREFS_NAME = "sinchprefs"
        private const val APP_KEY = "app_key"
        private const val APP_SECRET_KEY = "app_secret"
        private const val ENV_KEY = "environment"
        private const val USED_DEF_NAME_KEY = "def_pos_key"
    }

    private val preferences = customPrefs(appContext, PREFS_NAME)

    private val defaultConfigs by lazy {
        appContext.defaultConfigs
    }

    var usedConfig: AppConfig
        get() {
            val usedConfigName = usedConfigName
            return AppConfig(
                name = this.usedConfigName,
                appKey = appKey(usedConfigName),
                appSecret = "",
                environment = environment(usedConfigName),
                isCustom = usedConfigName == AppConfig.CUSTOM_CONFIG_NAME
            )
        }
        set(value) {
            value.let {
                usedConfigName = it.name
                updateAppKey(it.name, it.appKey)
                updateEnvironment(it.name, it.environment)
            }
        }

    var usedConfigName: String
        get() = preferences[USED_DEF_NAME_KEY, defaultConfigs.first().name]
        set(value) {
            preferences[USED_DEF_NAME_KEY] = value
        }

    private fun appKey(configName: String): String =
        preferences["${APP_KEY}_$configName", defaultConfigWithName(configName)?.appKey.orEmpty()]

    private fun updateAppKey(configName: String, newKey: String) {
        preferences["${APP_KEY}_$configName"] = newKey
    }

    private fun environment(configName: String): String =
        preferences["${ENV_KEY}_$configName", defaultConfigWithName(configName)?.environment.orEmpty()]

    private fun updateEnvironment(configName: String, newEnv: String) {
        preferences["${ENV_KEY}_$configName"] = newEnv
    }

    private fun defaultConfigWithName(name: String) = defaultConfigs.firstOrNull { it.name == name }

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