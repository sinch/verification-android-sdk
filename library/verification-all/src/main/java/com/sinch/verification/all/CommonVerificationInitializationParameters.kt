package com.sinch.verification.all

import android.app.Activity
import com.sinch.verification.core.VerificationInitData
import com.sinch.verification.core.config.general.GlobalConfig
import com.sinch.verification.core.initiation.response.EmptyInitializationListener
import com.sinch.verification.core.initiation.response.InitiationListener
import com.sinch.verification.core.initiation.response.InitiationResponseData
import com.sinch.verification.core.verification.response.EmptyVerificationListener
import com.sinch.verification.core.verification.response.VerificationListener

/**
 * Container for all the properties needed to initialize any type of verification. Used by [BasicVerificationMethodBuilder] to create
 * a verification method based on [VerificationInitData.usedMethod] field.
 * @property globalConfig Global configuration instance.
 * @property verificationInitData Initialization data reference.
 * @property initiationListener Initialization process listener reference.
 * @property verificationListener Verification process listener reference.
 * @property activity Activity required for the seamless v2 digital credential picker UI. Only needed when v2 responses are expected.
 */
class CommonVerificationInitializationParameters(
    val globalConfig: GlobalConfig,
    val verificationInitData: VerificationInitData,
    val initiationListener: InitiationListener<InitiationResponseData> = EmptyInitializationListener(),
    val verificationListener: VerificationListener = EmptyVerificationListener(),
    val activity: Activity? = null
)