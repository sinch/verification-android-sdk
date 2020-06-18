package com.sinch.verification.all

import com.sinch.smsverification.SmsVerificationMethod
import com.sinch.smsverification.config.SmsVerificationConfig
import com.sinch.verification.callout.CalloutVerificationMethod
import com.sinch.verification.callout.config.CalloutVerificationConfig
import com.sinch.verification.flashcall.FlashCallVerificationMethod
import com.sinch.verification.flashcall.config.FlashCallVerificationConfig
import com.sinch.verification.seamless.SeamlessVerificationMethod
import com.sinch.verification.seamless.config.SeamlessVerificationConfig
import com.sinch.verificationcore.internal.Verification
import com.sinch.verificationcore.internal.VerificationMethodType

/**
 * Helper object creating any type of [Verification] based on [CommonVerificationInitializationParameters].
 */
object BasicVerificationMethodBuilder {

    /**
     * Creates a [Verification] instance.
     * @param commonVerificationInitializationParameters Properties of Verification that are used by each verification method builder.
     * @param appHash Optional application hash needed by [SmsVerificationMethod].
     */
    @JvmStatic
    fun createVerification(
        commonVerificationInitializationParameters: CommonVerificationInitializationParameters,
        appHash: String?
    ) = when (commonVerificationInitializationParameters.verificationInitData.usedMethod) {
        VerificationMethodType.SMS -> commonVerificationInitializationParameters.asSmsVerification(
            appHash = appHash
        )
        VerificationMethodType.FLASHCALL -> commonVerificationInitializationParameters.asFlashcallVerification()
        VerificationMethodType.CALLOUT -> commonVerificationInitializationParameters.asCalloutVerification()
        VerificationMethodType.SEAMLESS -> commonVerificationInitializationParameters.asSeamlessVerification()
    }
}

private fun CommonVerificationInitializationParameters.asSmsVerification(appHash: String?) =
    SmsVerificationMethod.Builder().config(
        SmsVerificationConfig.Builder()
            .globalConfig(globalConfig)
            .withVerificationProperties(verificationInitData)
            .appHash(appHash)
            .build()
    )
        .initializationListener(initiationListener)
        .verificationListener(verificationListener)
        .build()

private fun CommonVerificationInitializationParameters.asFlashcallVerification() =
    FlashCallVerificationMethod.Builder().config(
        FlashCallVerificationConfig.Builder()
            .globalConfig(globalConfig)
            .withVerificationProperties(verificationInitData)
            .build()
    )
        .initializationListener(initiationListener)
        .verificationListener(verificationListener)
        .build()

private fun CommonVerificationInitializationParameters.asCalloutVerification() =
    CalloutVerificationMethod.Builder().config(
        CalloutVerificationConfig.Builder()
            .globalConfig(globalConfig)
            .withVerificationProperties(verificationInitData)
            .build()
    )
        .initializationListener(initiationListener)
        .verificationListener(verificationListener)
        .build()

private fun CommonVerificationInitializationParameters.asSeamlessVerification() =
    SeamlessVerificationMethod.Builder().config(
        SeamlessVerificationConfig.Builder()
            .globalConfig(globalConfig)
            .withVerificationProperties(verificationInitData)
            .build()
    )
        .initializationListener(initiationListener)
        .verificationListener(verificationListener)
        .build()