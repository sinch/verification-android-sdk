package com.sinch.verification.core

import com.sinch.logging.logger
import com.sinch.verification.core.config.GlobalConfigSetter
import com.sinch.verification.core.config.InitialSetter
import com.sinch.verification.core.config.VerificationMethodConfigCreator
import com.sinch.verification.core.config.VerificationMethodConfigCreatorParameters
import com.sinch.verification.core.verification.VerificationLanguage

abstract class BaseVerificationMethodConfigBuilder<T : VerificationMethodConfigCreator<T, *>> :
    GlobalConfigSetter<T>,
    InitialSetter<T>,
    VerificationMethodConfigCreatorParameters<T> {

    protected val logger = logger()

    protected var number: String? = null
    protected var honourEarlyReject: Boolean = true
    protected var custom: String? = null
    protected var reference: String? = null
    protected var acceptedLanguages: List<VerificationLanguage> = emptyList()

}