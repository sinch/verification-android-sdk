package com.sinch.verification.flashcall.initialization

import com.sinch.verification.core.initiation.response.InitiationDetails
import com.sinch.verification.core.internal.VerificationMethodType
import com.sinch.verification.core.verification.interceptor.CodeInterceptionTimeoutException
import com.sinch.verification.flashcall.report.FlashCallReportData
import com.sinch.verification.flashcall.verification.matcher.FlashCallPatternMatcher
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Class containing details (returned by the API) about the initiated flashcall verification process.
 * @property cliFilter Template (regex) used by [FlashCallPatternMatcher] to match incoming phone call number.
 * @property interceptionTimeout Maximum timeout after which interceptor should report [CodeInterceptionTimeoutException].
 * @property reportTimeout Timeout after which SDK stops reporting late calls.
 * @property subVerificationId Id assigned to each verification method that can be used in case of [VerificationMethodType.AUTO].
 * @see FlashCallReportData
 */
@Serializable
data class FlashCallInitializationDetails(
    @SerialName("cliFilter") val cliFilter: String,
    @SerialName("interceptionTimeout") val interceptionTimeoutApi: Long?,
    @SerialName("reportTimeout") val reportTimeoutApi: Long?,
    @SerialName("denyCallAfter") val denyCallAfter: Long?,
    @SerialName("cli") val cli: String? = null,
    @SerialName("subVerificationId") override val subVerificationId: String? = null
) : InitiationDetails {

    companion object {

        /**
         * Default interception timeout in seconds (used when API returns NULL).
         */
        const val DEFAULT_INTERCEPTION_TIMEOUT = 15000L //in seconds
    }

    /**
     * Interception timeout to be used by the SDK (handles NULL timeout returned by the API).
     */
    val interceptionTimeout: Long
        get() = (interceptionTimeoutApi?.times(1000) ?: 0).let {
            if (it > 0) it else DEFAULT_INTERCEPTION_TIMEOUT
        }

    /**
     * Report timeout to be used by the SDK (handles NULL cases and report timeout being grater then
     * interception timeout).
     */
    val reportTimeout: Long
        get() = maxOf(interceptionTimeout, (reportTimeoutApi?.times(1000) ?: 0))

}