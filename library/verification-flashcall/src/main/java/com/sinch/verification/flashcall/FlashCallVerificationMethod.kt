package com.sinch.verification.flashcall

import com.sinch.utils.permission.Permission
import com.sinch.utils.permission.PermissionUtils
import com.sinch.verification.flashcall.config.FlashCallVerificationConfig
import com.sinch.verification.flashcall.initialization.FlashCallInitializationListener
import com.sinch.verification.flashcall.initialization.FlashCallInitializationResponseData
import com.sinch.verification.flashcall.initialization.FlashCallVerificationInitializationData
import com.sinch.verification.flashcall.report.FlashCallReportData
import com.sinch.verification.flashcall.report.FlashCallReportDetails
import com.sinch.verification.flashcall.verification.FlashCallVerificationData
import com.sinch.verification.flashcall.verification.FlashCallVerificationDetails
import com.sinch.verification.flashcall.verification.callhistory.ContentProviderCallHistoryReader
import com.sinch.verification.flashcall.verification.interceptor.CodeInterceptionState
import com.sinch.verification.flashcall.verification.interceptor.FlashCallInterceptor
import com.sinch.verification.flashcall.verification.matcher.FlashCallPatternMatcher
import com.sinch.verificationcore.config.method.VerificationMethod
import com.sinch.verificationcore.config.method.VerificationMethodCreator
import com.sinch.verificationcore.initiation.InitiationApiCallback
import com.sinch.verificationcore.initiation.VerificationIdentity
import com.sinch.verificationcore.initiation.response.EmptyInitializationListener
import com.sinch.verificationcore.internal.Verification
import com.sinch.verificationcore.internal.error.VerificationException
import com.sinch.verificationcore.internal.utils.enqueue
import com.sinch.verificationcore.verification.IgnoredUnitApiCallback
import com.sinch.verificationcore.verification.VerificationApiCallback
import com.sinch.verificationcore.verification.VerificationSourceType
import com.sinch.verificationcore.verification.response.EmptyVerificationListener
import com.sinch.verificationcore.verification.response.VerificationListener
import java.util.*

typealias EmptyFlashCallInitializationListener = EmptyInitializationListener<FlashCallInitializationResponseData>
typealias SimpleInitializationFlashCallApiCallback = InitiationApiCallback<FlashCallInitializationResponseData>

class FlashCallVerificationMethod private constructor(
    private val config: FlashCallVerificationConfig,
    private val initializationListener: FlashCallInitializationListener = EmptyFlashCallInitializationListener(),
    verificationListener: VerificationListener = EmptyVerificationListener()
) : VerificationMethod<FlashCallVerificationService>(config, verificationListener) {

    private val requestDataData: FlashCallVerificationInitializationData
        get() =
            FlashCallVerificationInitializationData(
                identity = VerificationIdentity(config.number),
                honourEarlyReject = config.honourEarlyReject,
                custom = config.custom,
                metadata = config.metadataFactory.create()
            )

    private var flashCallInterceptor: FlashCallInterceptor? = null
    private var initiationStartDate = Date()

    override fun onPreInitiate(): Boolean {
        if (!PermissionUtils.isPermissionGranted(globalConfig.context, Permission.READ_CALL_LOG)) {
            initializationListener.onInitializationFailed(VerificationException("Missing ${Permission.READ_CALL_LOG}"))
            return false
        }
        initiationStartDate = Date()
        return true
    }

    override fun onInitiate() {
        verificationService.initializeVerification(requestDataData).enqueue(
            retrofit = retrofit,
            apiCallback = SimpleInitializationFlashCallApiCallback(
                listener = initializationListener,
                statusListener = this,
                successCallback = { initializeInterceptor(it) })
        )
    }

    override fun onVerify(verificationCode: String, sourceType: VerificationSourceType) {
        verificationService.verifyNumber(
            number = config.number,
            data = FlashCallVerificationData(
                sourceType,
                FlashCallVerificationDetails(verificationCode)
            )
        ).enqueue(retrofit, VerificationApiCallback(verificationListener, this))
    }

    override fun report() {
        super.report()
        flashCallInterceptor?.let {
            verificationService.reportVerification(
                config.number, FlashCallReportData(
                    FlashCallReportDetails(
                        isLateCall = it.codeInterceptionState == CodeInterceptionState.LATE,
                        isNoCall = it.codeInterceptionState == CodeInterceptionState.NONE
                    )
                )
            ).enqueue(retrofit, IgnoredUnitApiCallback())
        }
    }

    private fun initializeInterceptor(data: FlashCallInitializationResponseData) {
        try {
            flashCallInterceptor = FlashCallInterceptor(
                context = config.globalConfig.context,
                interceptionTimeout = chooseMaxTimeout(
                    userDefined = config.maxTimeout,
                    apiResponseTimeout = data.details.interceptionTimeout
                ),
                reportTimeout = data.details.reportTimeout,
                interceptionListener = this,
                flashCallPatternMatcher = FlashCallPatternMatcher(data.details.cliFilter),
                callHistoryReader = ContentProviderCallHistoryReader(config.globalConfig.context.contentResolver),
                callHistoryStartDate = initiationStartDate
            )
            flashCallInterceptor?.start()
        } catch (e: Exception) {
            verificationListener.onVerificationFailed(e)
        }
    }

    override fun onCodeIntercepted(code: String, source: VerificationSourceType) {
        verify(code, source)
    }

    override fun onCodeInterceptionError(e: Throwable) {
        verificationListener.onVerificationFailed(e)
    }

    class Builder private constructor() :
        VerificationMethodCreator<FlashCallInitializationListener>,
        FlashCallVerificationConfigSetter {

        companion object {
            @JvmStatic
            val instance: FlashCallVerificationConfigSetter
                get() = Builder()
        }

        private var initializationListener: FlashCallInitializationListener =
            EmptyFlashCallInitializationListener()
        private var verificationListener: VerificationListener = EmptyVerificationListener()

        private lateinit var config: FlashCallVerificationConfig

        override fun config(config: FlashCallVerificationConfig): VerificationMethodCreator<FlashCallInitializationListener> =
            apply {
                this.config = config
            }

        override fun verificationListener(verificationListener: VerificationListener): VerificationMethodCreator<FlashCallInitializationListener> =
            apply {
                this.verificationListener = verificationListener
            }

        override fun initializationListener(initializationListener: FlashCallInitializationListener): VerificationMethodCreator<FlashCallInitializationListener> =
            apply {
                this.initializationListener = initializationListener
            }

        override fun build(): Verification {
            return FlashCallVerificationMethod(
                config = config,
                initializationListener = initializationListener,
                verificationListener = verificationListener
            )
        }

    }

}