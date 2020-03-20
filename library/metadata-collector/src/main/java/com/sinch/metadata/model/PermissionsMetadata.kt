package com.sinch.metadata.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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