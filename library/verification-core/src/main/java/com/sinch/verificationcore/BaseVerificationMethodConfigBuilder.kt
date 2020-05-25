package com.sinch.verificationcore

import com.sinch.logging.logger
import com.sinch.verificationcore.config.GlobalConfigSetter
import com.sinch.verificationcore.config.NumberSetter
import com.sinch.verificationcore.config.VerificationMethodConfigCreator
import com.sinch.verificationcore.config.VerificationMethodConfigCreatorParameters
import java.util.*

abstract class BaseVerificationMethodConfigBuilder<T : VerificationMethodConfigCreator<T, *>> :
    GlobalConfigSetter<T>,
    NumberSetter<T>,
    VerificationMethodConfigCreatorParameters<T> {

    protected val logger = logger()

    protected lateinit var number: String

    protected var honourEarlyReject: Boolean = true
    protected var custom: String? = null
    protected var reference: String? = null
    protected var acceptedLanguages: List<Locale> = emptyList()

}