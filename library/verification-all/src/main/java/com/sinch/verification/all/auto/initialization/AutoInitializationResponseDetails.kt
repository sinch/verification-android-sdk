package com.sinch.verification.all.auto.initialization

import com.sinch.verification.core.initiation.response.InitiationDetails
import com.sinch.verification.core.internal.VerificationMethodType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AutoInitializationResponseDetails(
    @SerialName("subVerificationId") override val subVerificationId: String? = null,
    @SerialName("methodsOrder") val methodsOrder: List<VerificationMethodType>
) :
    InitiationDetails {

    fun methodAfter(method: VerificationMethodType?): VerificationMethodType? {
        return if (method == null) {
            methodsOrder.firstOrNull()
        } else {
            methodsOrder.getOrNull(methodsOrder.indexOf(method) + 1)
        }
    }

}