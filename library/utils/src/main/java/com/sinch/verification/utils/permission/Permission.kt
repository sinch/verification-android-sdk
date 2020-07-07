package com.sinch.verification.utils.permission

import android.Manifest

/**
 * Enum representing Android permissions (more convenient way then using Android string constants).
 */
enum class Permission {
    ACCESS_NETWORK_STATE,
    CHANGE_NETWORK_STATE,
    READ_PHONE_STATE,
    READ_CALL_LOG,
    CALL_PHONE,
    READ_SMS,
    RECEIVE_SMS,
    ACCESS_COARSE_LOCATION,
    ACCESS_FINE_LOCATION;

    /**
     * String constant used by Android framework.
     */
    val androidValue: String
        get() = when (this) {
            ACCESS_NETWORK_STATE -> Manifest.permission.ACCESS_NETWORK_STATE
            CHANGE_NETWORK_STATE -> Manifest.permission.CHANGE_NETWORK_STATE
            READ_PHONE_STATE -> Manifest.permission.READ_PHONE_STATE
            READ_CALL_LOG -> Manifest.permission.READ_CALL_LOG
            CALL_PHONE -> Manifest.permission.CALL_PHONE
            READ_SMS -> Manifest.permission.READ_SMS
            RECEIVE_SMS -> Manifest.permission.RECEIVE_SMS
            ACCESS_COARSE_LOCATION -> Manifest.permission.ACCESS_COARSE_LOCATION
            ACCESS_FINE_LOCATION -> Manifest.permission.ACCESS_FINE_LOCATION
        }

}