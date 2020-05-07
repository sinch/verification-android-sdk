package com.sinch.verificationcore.query

import com.sinch.verificationcore.config.general.GlobalConfig
import com.sinch.verificationcore.internal.VerificationMethodType
import com.sinch.verificationcore.internal.utils.enqueue
import com.sinch.verificationcore.query.callback.VerificationInfoApiCallback
import com.sinch.verificationcore.query.callback.VerificationInfoCallback
import com.sinch.verificationcore.verification.response.VerificationResponseData
import retrofit2.Call

/**
 * [VerificationQuery] that uses [VerificationQueryService] for accessing information about verifications.
 */
class SinchVerificationQuery internal constructor(private val globalConfig: GlobalConfig) :
    VerificationQuery {

    private val verificationQueryService: VerificationQueryService by lazy {
        globalConfig.retrofit.create(VerificationQueryService::class.java)
    }

    override fun queryById(id: String, callback: VerificationInfoCallback) {
        verificationQueryService.queryById(id).enqueueVerificationInfo(callback)
    }

    override fun queryByReference(reference: String, callback: VerificationInfoCallback) {
        verificationQueryService.queryByReference(reference).enqueueVerificationInfo(callback)
    }

    override fun queryByEndpoint(
        method: VerificationMethodType,
        number: String,
        callback: VerificationInfoCallback
    ) {
        verificationQueryService.queryByEndpoint(method, number).enqueueVerificationInfo(callback)
    }

    private fun Call<VerificationResponseData>.enqueueVerificationInfo(callback: VerificationInfoCallback) {
        enqueue(
            retrofit = globalConfig.retrofit,
            apiCallback = VerificationInfoApiCallback(callback)
        )
    }

    companion object {
        /**
         * Creates [VerificationQuery] instance based on provided configuration.
         * @param globalConfig Reference to global configuration of Sinch SDK.
         * @return [VerificationQuery] instance.
         */
        @JvmStatic
        fun withGlobalConfig(globalConfig: GlobalConfig): VerificationQuery =
            SinchVerificationQuery(globalConfig)
    }
}