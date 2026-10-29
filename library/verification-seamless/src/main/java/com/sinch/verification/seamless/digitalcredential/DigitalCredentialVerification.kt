@file:OptIn(ExperimentalDigitalCredentialApi::class)

package com.sinch.verification.seamless.digitalcredential

import android.app.Activity
import androidx.credentials.CredentialManager
import androidx.credentials.DigitalCredential
import androidx.credentials.ExperimentalDigitalCredentialApi
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetDigitalCredentialOption
import com.sinch.logging.logger
import com.sinch.verification.core.config.general.GlobalConfig
import com.sinch.verification.core.internal.error.VerificationException
import com.sinch.verification.seamless.SeamlessVerificationStrategy
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

/**
 * v2 seamless strategy. Requests a digital credential via the Android Credential Manager and
 * reports the resulting operator token to the caller.
 * The supplied [activity] is held weakly and is not tracked across Activity recreation.
 * @param nonce Optional nonce included in the digital credential request.
 * @param globalConfig Global config providing json, context and other shared settings.
 * @param activity Activity used to launch the credential picker; held weakly, may be null.
 * @param onLocalFailure Invoked when the flow fails locally; the caller owns failure reporting.
 * @param onLocalSuccess Invoked with the operator token; the caller owns reporting it.
 */
class DigitalCredentialVerification(
    private val nonce: String?,
    private val globalConfig: GlobalConfig,
    activity: Activity?,
    private val onLocalFailure: (Throwable) -> Unit,
    private val onLocalSuccess: (String) -> Unit
) : SeamlessVerificationStrategy {

    private val logger = logger()
    private val verificationScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var credentialJob: Job? = null
    private var stopped = false
    private val mapper = DigitalCredentialMapper(globalConfig.json)
    private val activityRef: WeakReference<Activity>? = activity?.let { WeakReference(it) }

    private val credentialManager by lazy {
        CredentialManager.create(globalConfig.context)
    }

    override fun start(payload: String) {
        val activityContext = activityRef?.get()
        if (activityContext == null) {
            onLocalFailure(
                VerificationException(
                    "v2 seamless verification requires an Activity context for the credential picker. " +
                        "Provide one via SeamlessVerificationConfig.Builder.activity() or AutoVerificationConfig.Builder.activity()"
                )
            )
            return
        }
        stopped = false
        credentialJob = verificationScope.launch {
            try {
                val requestJson = mapper.buildRequestJson(payload, nonce)
                logger.debug("DigitalCredential requestJson: $requestJson")
                val request = GetCredentialRequest(
                    listOf(GetDigitalCredentialOption(requestJson = requestJson))
                )
                val credentialResponse = credentialManager.getCredential(
                    context = activityContext,
                    request = request
                )
                val credential = credentialResponse.credential as? DigitalCredential
                    ?: throw VerificationException(
                        "Unexpected credential type: ${credentialResponse.credential.type}"
                    )
                logger.debug("DigitalCredential credentialJson: ${credential.credentialJson}")
                onLocalSuccess(credential.credentialJson)
            } catch (e: CancellationException) {
                logger.debug("DigitalCredential request cancelled: ${e.message}")
                if (!stopped) {
                    onLocalFailure(e)
                }
            } catch (e: Exception) {
                onLocalFailure(e)
            }
        }
    }

    override fun stop() {
        stopped = true
        credentialJob?.cancel()
    }
}
