package com.sinch.verificationcore.config.method

import com.sinch.verificationcore.config.general.GeneralConfig
import com.sinch.verificationcore.initiation.response.InitiationResponseData
import com.sinch.verificationcore.internal.Verification

abstract class VerificationMethod<Service>(
    verificationServiceConfig: VerificationMethodConfig<Service>
) :
    Verification {

    protected val generalConfig: GeneralConfig = verificationServiceConfig.config
    protected val verificationService: Service = verificationServiceConfig.apiService

    protected var initResponseData: InitiationResponseData? = null

    protected val id: String? get() = initResponseData?.id

}