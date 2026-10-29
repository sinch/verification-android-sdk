package com.sinch.verification.seamless.config

import android.app.Activity
import com.sinch.metadata.AndroidMetadataFactory
import com.sinch.verification.core.BaseVerificationMethodConfigBuilder
import com.sinch.verification.core.config.GlobalConfigSetter
import com.sinch.verification.core.config.InitialSetter
import com.sinch.verification.core.config.general.GlobalConfig
import com.sinch.verification.core.config.method.VerificationMethodConfig
import com.sinch.verification.core.internal.Verification
import com.sinch.verification.core.verification.VerificationLanguage
import com.sinch.verification.seamless.SeamlessVerificationMethod
import com.sinch.verification.seamless.SeamlessVerificationService
import com.sinch.verificationcore.BuildConfig
import java.lang.ref.WeakReference

/**
 * Configuration used by [SeamlessVerificationMethod] to handle seamless verification.
 * @param globalConfig Global SDK configuration reference.
 * @param number Phone number that needs be verified.
 * @property honourEarlyReject Flag indicating if the verification process should honour early rejection rules.
 * @property custom Custom string that is passed with the initiation request.
 * @property activity Activity used as context for the v2 digital credential picker UI. Not required for v1.
 */
class SeamlessVerificationConfig internal constructor(
    globalConfig: GlobalConfig,
    number: String?,
    honourEarlyReject: Boolean = true,
    custom: String? = null,
    reference: String? = null,
    activity: Activity? = null
) : VerificationMethodConfig<SeamlessVerificationService>(
    globalConfig = globalConfig,
    number = number,
    honourEarlyReject = honourEarlyReject,
    custom = custom,
    reference = reference,
    apiService = globalConfig.retrofit.create(SeamlessVerificationService::class.java),
    acceptedLanguages = emptyList(),
    metadataFactory = AndroidMetadataFactory(
        globalConfig.context,
        BuildConfig.SINCH_SDK_VERSION_NAME,
        BuildConfig.FLAVOR
    )
) {
    private val activityRef: WeakReference<Activity>? = activity?.let { WeakReference(it) }

    /** Returns the [Activity] for the v2 credential picker, or null if not set or already collected. */
    internal val activityContext: Activity? get() = activityRef?.get()

    class Builder private constructor() :
        BaseVerificationMethodConfigBuilder<SeamlessVerificationConfigCreator>(),
        SeamlessVerificationConfigCreator {

        companion object {

            /**
             * Instance of builder that should be used to create [SeamlessVerificationConfig] object.
             */
            @JvmStatic
            val instance: GlobalConfigSetter<SeamlessVerificationConfigCreator>
                get() = Builder()

            operator fun invoke() = instance

        }

        private lateinit var globalConfig: GlobalConfig
        private var activity: Activity? = null

        /**
         * Builds [SeamlessVerificationConfig] instance.
         * @return [SeamlessVerificationConfig] instance with previously defined parameters.
         */
        override fun build(): SeamlessVerificationConfig =
            SeamlessVerificationConfig(
                globalConfig = globalConfig,
                number = number,
                honourEarlyReject = honourEarlyReject,
                custom = custom,
                reference = reference,
                activity = activity
            )

        /**
         * Assigns honourEarlyReject flag to the builder.
         * @param honourEarlyReject Flag indicating if the verification process should honour early rejection rules.
         * @return Instance of builder with assigned flag.
         */
        override fun honourEarlyReject(honourEarlyReject: Boolean) = apply {
            this.honourEarlyReject = honourEarlyReject
        }

        /**
         * Assigns custom string to the builder.
         * @param custom Custom string that is passed with the initiation request.
         * @return Instance of builder with assigned custom field.
         */
        override fun custom(custom: String?) = apply {
            this.custom = custom
        }

        /**
         * Assigns reference string to the builder.
         * @param reference Reference string that is passed with the initiation request for tracking purposes.
         * @return Instance of builder with assigned reference field.
         */
        override fun reference(reference: String?) = apply {
            this.reference = reference
        }

        /**
         * Assigns globalConfig value to the builder.
         * @param globalConfig Global SDK configuration reference.
         * @return Instance of builder with assigned globalConfig field.
         */
        override fun globalConfig(globalConfig: GlobalConfig): InitialSetter<SeamlessVerificationConfigCreator> =
            apply {
                this.globalConfig = globalConfig
            }

        /**
         * Assigns number value to the builder.
         * @param number Phone number that needs be verified.
         * @return Instance of builder with assigned number field.
         */
        override fun number(number: String): SeamlessVerificationConfigCreator = apply {
            this.number = number
        }

        /**
         * Provides the Activity context needed to display the v2 credential picker UI.
         * Call this when your app may perform a v2 seamless verification — without it,
         * the v2 flow will fail at runtime.
         * @param activity The currently visible Activity that will host the credential picker.
         * @return Instance of builder with assigned activity.
         */
        override fun activity(activity: Activity): SeamlessVerificationConfigCreator = apply {
            this.activity = activity
        }

        /**
         * Allows to build a [Verification] that skips local verification initialization request (POST /verifications).
         * In such a case it's not needed to pass a number to verify and app can proceed to verify the number directly.
         * In such a flow a verification could be initialized externally and the SDK can used to execute just the
         * verification request. To do that pass the received 'targetUrl' parameter directly to the
         * [Verification.verify] method.
         */
        override fun skipLocalInitialization(): SeamlessVerificationConfigCreator = this

        override fun acceptedLanguages(acceptedLanguages: List<VerificationLanguage>) =
            this.also {
                logger.debug("This verification method currently does not support accepted languages")
            }

    }

}
