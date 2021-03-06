package com.sinch.verification.core.config.method

import com.sinch.logging.logger
import com.sinch.verification.core.config.general.GlobalConfig
import com.sinch.verification.core.initiation.response.InitiationResponseData
import com.sinch.verification.core.internal.*
import com.sinch.verification.core.verification.interceptor.CodeInterceptionListener
import com.sinch.verification.core.verification.model.VerificationSourceType
import com.sinch.verification.core.verification.response.EmptyVerificationListener
import com.sinch.verification.core.verification.response.VerificationListener

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
    final override fun verify(verificationCode: String, method: VerificationMethodType?) {
        verify(verificationCode, VerificationSourceType.MANUAL, method)
    }

    /**
     * Verifies the verification code/
     * @param verificationCode Code to be verified.
     * @param sourceType Source of the verification code.
     */
    fun verify(
        verificationCode: String,
        sourceType: VerificationSourceType,
        method: VerificationMethodType? = null
    ) {
        if (verificationState.canVerify) {
            logger.info("Verify called with code: $verificationCode for method: $method acquired from source: $sourceType")
            update(VerificationState.Verification(VerificationStateStatus.ONGOING))
            onVerify(verificationCode, sourceType, method)
        } else {
            logger.warn("Verify called however verificationState.canVerify returned false")
        }
    }

    final override fun initiate() {
        if (onPreInitiate() && verificationState.canInitiate) {
            logger.debug("Initiating verification")
            update(VerificationState.Initialization(VerificationStateStatus.ONGOING, null))
            onInitiate()
        } else {
            logger.warn("Initiate called however onPreInitiate or verificationState.canInitiate returned false")
        }
    }

    override fun stop() {
        if (verificationState.isVerificationProcessFinished) {
            logger.info("Verification process already finished with state $verificationState")
            return
        }
        logger.info("Stop called")
        update(VerificationState.ManuallyStopped)
    }

    /**
     * Updates verification state.
     * @param newState New verification state.
     */
    override fun update(newState: VerificationState) {
        logger.debug("Verification state update $verificationState -> $newState")
        this.verificationState = newState
        if (newState is VerificationState.Initialization &&
            newState.initiationResponseData != null
        ) {
            this.initResponseData = newState.initiationResponseData
        }
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
    protected abstract fun onVerify(
        verificationCode: String,
        sourceType: VerificationSourceType,
        method: VerificationMethodType?
    )

    /**
     * Function called when entire verification code interception process has stopped.
     */
    override fun onCodeInterceptionStopped() {
        if (verificationState != VerificationState.ManuallyStopped) {
            report()
        }
    }

}