package com.sinch.sinchverification

import android.os.Parcelable
import com.sinch.verification.callout.initialization.CalloutInitializationListener
import com.sinch.verificationcore.config.method.VerificationMethodProperties
import com.sinch.verificationcore.internal.VerificationMethodType
import kotlinx.android.parcel.Parcelize

@Parcelize
data class VerificationInitData(
    val usedMethod: VerificationMethodType,
    override val number: String,
    override val custom: String?,
    override val honourEarlyReject: Boolean,
    override val maxTimeout: Long?,
    override val acceptedLanguages: List<String>
) : VerificationMethodProperties, Parcelable