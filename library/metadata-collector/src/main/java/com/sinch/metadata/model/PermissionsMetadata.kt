package com.sinch.metadata.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Class holding metadata describing which permissions user has granted to the application.
 * @property hasReadPhoneStatePermission Flag indicating if application has READ_PHONE_STATE permission.
 * @property hasReadCallLogPermission Flag indicating if application has READ_CALL_LOG permission.
 * @property hasCallPhonePermission Flag indicating if application has CALL_PHONE permission.
 * @property hasReadSmsPermission Flag indicating if application has READ_SMS permission.
 * @property hasReceiveSmsPermission Flag indicating if application has RECEIVE_SMS permission.
 * @property hasAccessNetworkStatePermission Flag indicating if application has ACCESS_NETWORK_STATE permission.
 * @property hasCellularSignalPermission Flag indicating if application has ACCESS_COARSE_LOCATION or ACCESS_FINE_LOCATION permission.
 */
@Serializable
data class PermissionsMetadata(
    @SerialName("READ_PHONE_STATE") val hasReadPhoneStatePermission: Boolean,
    @SerialName("READ_CALL_LOG") val hasReadCallLogPermission: Boolean,
    @SerialName("CALL_PHONE") val hasCallPhonePermission: Boolean,
    @SerialName("READ_SMS") val hasReadSmsPermission: Boolean,
    @SerialName("RECEIVE_SMS") val hasReceiveSmsPermission: Boolean,
    @SerialName("ACCESS_NETWORK_STATE") val hasAccessNetworkStatePermission: Boolean,
    @SerialName("getCellularSignalLevel") val hasCellularSignalPermission: Boolean
)