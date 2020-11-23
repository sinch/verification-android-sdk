package com.sinch.verification.core

import android.os.Parcelable
import com.sinch.verification.core.config.method.VerificationMethodProperties
import com.sinch.verification.core.internal.VerificationMethodType
import com.sinch.verification.core.verification.VerificationLanguage
import kotlinx.android.parcel.Parcelize

@Parcelize
data class VerificationInitData(
    val usedMethod: VerificationMethodType,
    override val number: String,
    override val custom: String?,
    override val reference: String?,
    override val honourEarlyReject: Boolean,
    override val acceptedLanguages: List<VerificationLanguage>
) : VerificationMethodProperties, Parcelable