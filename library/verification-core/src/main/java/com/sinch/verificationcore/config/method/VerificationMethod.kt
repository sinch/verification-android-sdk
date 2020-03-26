package com.sinch.verificationcore.config.method

import com.sinch.verificationcore.config.general.GlobalConfig
import com.sinch.verificationcore.initiation.response.InitiationResponseData
import com.sinch.verificationcore.internal.Verification
import com.sinch.verificationcore.verification.VerificationSourceType
import com.sinch.verificationcore.verification.response.EmptyVerificationListener
import com.sinch.verificationcore.verification.response.VerificationListener

abstract class VerificationMethod<Service>(
    verificationServiceConfig: VerificationMethodConfig<Service>,
    protected val verificationListener: VerificationListener = EmptyVerificationListener()
) :
    Verification {

    protected val globalConfig: GlobalConfig = verificationServiceConfig.globalConfig
    protected val verificationService: Service = verificationServiceConfig.apiService

    protected var initResponseData: InitiationResponseData? = null

    protected val id: String? get() = initResponseData?.id

    override fun verify(verificationCode: String) {
        verify(verificationCode, VerificationSourceType.MANUAL)
    }

    protected abstract fun verify(verificationCode: String, sourceType: VerificationSourceType)

}