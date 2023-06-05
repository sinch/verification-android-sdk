package com.sinch.verification.core.config

import com.sinch.verification.core.config.general.GlobalConfig
import com.sinch.verification.core.config.method.VerificationMethodProperties
import com.sinch.verification.core.internal.Verification

interface GlobalConfigSetter<LastSetter : VerificationMethodConfigCreator<LastSetter, *>> {
    fun globalConfig(globalConfig: GlobalConfig): InitialSetter<LastSetter>
}

interface InitialSetter<LastSetter : VerificationMethodConfigCreator<LastSetter, *>> {

    /**
     * Convenient function to populate the config with common verification method at once.
     * @param verificationMethodProperties Container reference with common properties.
     */
    fun withVerificationProperties(verificationMethodProperties: VerificationMethodProperties): LastSetter =
        verificationMethodProperties.let {
            val number = it.number
            require(number != null) {
                "When using verificationMethodProperties number has to be specified explicitly."
            }
            number(number)
                .custom(it.custom)
                .honourEarlyReject(it.honourEarlyReject)
                .acceptedLanguages(it.acceptedLanguages)
                .reference(it.reference)
        }

    fun number(number: String): LastSetter

    /**
     * Allows to build a [Verification] that skips local verification initialization request (POST /verifications).
     * In such a case it's not needed to pass a number to verify and app can proceed to verify the verification
     * code directly. In such a flow a verification could be initialized externally and the SDK can used to
     * execute just the verification request. Currently only DATA verification is supported where the code
     * passed to verification method is the 'targetUrl' parameter of the initiation response.
     */
    fun skipLocalInitialization(): LastSetter
}