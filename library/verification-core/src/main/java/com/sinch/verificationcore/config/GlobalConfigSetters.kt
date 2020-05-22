package com.sinch.verificationcore.config

import com.sinch.verificationcore.config.general.GlobalConfig
import com.sinch.verificationcore.config.method.VerificationMethodProperties

interface GlobalConfigSetter<LastSetter : VerificationMethodConfigCreator<LastSetter, *>> {
    fun globalConfig(globalConfig: GlobalConfig): NumberSetter<LastSetter>
}

interface NumberSetter<LastSetter : VerificationMethodConfigCreator<LastSetter, *>> {

    /**
     * Convenient function to populate the config with common verification method at once.
     * @param verificationMethodProperties Container reference with common properties.
     */
    fun withVerificationProperties(verificationMethodProperties: VerificationMethodProperties): LastSetter =
        verificationMethodProperties.let {
            number(it.number)
                .custom(it.custom)
                .honourEarlyReject(it.honourEarlyReject)
                .acceptedLanguages(it.acceptedLanguages)
        }

    fun number(number: String): LastSetter
}