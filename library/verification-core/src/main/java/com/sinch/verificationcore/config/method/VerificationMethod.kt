package com.sinch.verificationcore.config.method

import com.sinch.logging.logger
import com.sinch.verificationcore.config.general.GlobalConfig
import com.sinch.verificationcore.initiation.response.InitiationResponseData
import com.sinch.verificationcore.internal.Verification
import com.sinch.verificationcore.internal.VerificationState
import com.sinch.verificationcore.internal.VerificationStateListener
import com.sinch.verificationcore.internal.VerificationStateStatus
import com.sinch.verificationcore.verification.VerificationSourceType
import com.sinch.verificationcore.verification.interceptor.CodeInterceptionListener
import com.sinch.verificationcore.verification.response.EmptyVerificationListener
import com.sinch.verificationcore.verification.response.VerificationListener

/**
 * Class containing common logic for every verification method.
 * @param verificationServiceConfig Verification method specific configuration reference.
 * @param Service Retrofit service class used for communication with the backend.
 * @param verificationListener Verification listener to be notified about verification process.
 */
abstract class VerificationMethod<Service>(
    verificationServiceConfig: VerificationMethodConfig<Service>,
    protected val verificationListener: VerificationListener = EmptyVerificationListener()
) :
    Verification, VerificationStateListener, CodeInterceptionListener {

    private var initResponseData: InitiationResponseData? = null

    protected val logger = logger()

    protected val globalConfig: GlobalConfig = verificationServiceConfig.globalConfig
    protected val verificationService: Service = verificationServiceConfig.apiService
    protected val retrofit get() = globalConfig.retrofit

    /**
     * Current state of the verification process.
     */
    final override var verificationState: VerificationState = VerificationState.IDLE

    /**
     * Id assigned to the verification request by sinch api or null if the process hasn't started yet.
     */
    protected val id: String? get() = initResponseData?.id

    /**
     * Verifies the code assuming it was typed manually.
     * @param verificationCode Code to be verified.
     */
    final override fun verify(verificationCode: String) {
        verify(verificationCode, VerificationSourceType.MANUAL)
    }

    /**
     * Verifies the verification code/
     * @param verificationCode Code to be verified.
     * @param sourceType Source of the verification code.
     */
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

    /**
     * Updates verification state.
     * @param newState New verification state.
     */
    override fun update(newState: VerificationState) {
        this.verificationState = newState
    }

    /**
     * Function called before [initiate]
     * @return true if the initiation process should begin, false otherwise
     */
    protected open fun onPreInitiate() = true

    /**
     * Reports the verification process result to Sinch backend.
     */
    protected open fun report() = Unit

    /**
     * Function called if the verification process is initiated. Verification method specific
     * API calls should be implemented here.
     */
    protected abstract fun onInitiate()

    /**
     * Function called when code needs to be verified. Verification method specific API calls
     * should be implemented here.
     */
    protected abstract fun onVerify(verificationCode: String, sourceType: VerificationSourceType)

    /**
     * Function called when entire verification process has stopped.
     */
    override fun onCodeInterceptionStopped() {
        report()
    }

    /**
     * Chooses the timeout after which verification method reports [CodeInterceptionException]
     * @param userDefined Timeout defined in the configuration process.
     * @param apiResponseTimeout Timeout returned by the API in initiation response data.
     */
    protected fun chooseMaxTimeout(userDefined: Long?, apiResponseTimeout: Long): Long {
        return if (userDefined == null) {
            apiResponseTimeout
        } else {
            if (apiResponseTimeout < userDefined) {
                logger.warn(
                    "Using api response timeout instead of config timeout " +
                            "as it is greater then max timeout returned by the API"
                )
            }
            minOf(apiResponseTimeout, userDefined)
        }
    }
}