package com.sinch.verificationcore.config.method

import com.sinch.logging.logger
import com.sinch.verificationcore.config.general.GlobalConfig
import com.sinch.verificationcore.initiation.response.InitiationResponseData
import com.sinch.verificationcore.internal.Verification
import com.sinch.verificationcore.internal.VerificationState
import com.sinch.verificationcore.internal.VerificationStateListener
import com.sinch.verificationcore.internal.VerificationStateStatus
import com.sinch.verificationcore.verification.VerificationSourceType
import com.sinch.verificationcore.verification.response.EmptyVerificationListener
import com.sinch.verificationcore.verification.response.VerificationListener

abstract class VerificationMethod<Service>(
    verificationServiceConfig: VerificationMethodConfig<Service>,
    protected val verificationListener: VerificationListener = EmptyVerificationListener()
) :
    Verification, VerificationStateListener {

    protected val logger = logger()

    protected val globalConfig: GlobalConfig = verificationServiceConfig.globalConfig
    protected val verificationService: Service = verificationServiceConfig.apiService
    protected val retrofit get() = globalConfig.retrofit

    protected var initResponseData: InitiationResponseData? = null

    final override var verificationState: VerificationState = VerificationState.IDLE

    protected val id: String? get() = initResponseData?.id

    final override fun verify(verificationCode: String) {
        verify(verificationCode, VerificationSourceType.MANUAL)
    }

    fun verify(verificationCode: String, sourceType: VerificationSourceType) {
        if (verificationState.canVerify) {
            verificationState = VerificationState.Verification(VerificationStateStatus.ONGOING)
            onVerify(verificationCode, sourceType)
        }
    }

    final override fun initiate() {
        if (onPreInitiate() && verificationState.canInitiate) {
            verificationState = VerificationState.Initialization(VerificationStateStatus.ONGOING)
            onInitiate()
        }
    }

    override fun update(newState: VerificationState) {
        this.verificationState = newState
    }

    protected open fun onPreInitiate() = true
    protected abstract fun onInitiate()
    protected abstract fun onVerify(verificationCode: String, sourceType: VerificationSourceType)

    protected fun chooseMaxTimeout(userDefined: Long?, apiResponseTimeout: Long?): Long? {
        val apiResponseTimeoutMs = apiResponseTimeout?.times(1000) //API uses seconds in timeouts
        if (userDefined == null) {
            return apiResponseTimeoutMs
        } else if (apiResponseTimeoutMs != null) {
            if (apiResponseTimeoutMs < userDefined) {
                logger.warn("Using api response timeout instead of config timeout " +
                        "as it is greater then max timeout returned by the API")
            }
            return minOf(apiResponseTimeoutMs, userDefined)
        }
        return apiResponseTimeoutMs
    }
}