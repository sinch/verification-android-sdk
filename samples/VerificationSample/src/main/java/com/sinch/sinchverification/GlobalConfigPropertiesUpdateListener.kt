package com.sinch.sinchverification

interface GlobalConfigPropertiesUpdateListener {
    fun onBaseURLUpdated(baseURL: String, isCustom: Boolean)
    fun onAppKeyUpdated(appKey: String)
}