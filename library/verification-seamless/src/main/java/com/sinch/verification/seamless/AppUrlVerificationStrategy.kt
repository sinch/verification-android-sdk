package com.sinch.verification.seamless

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import com.sinch.logging.logger
import com.sinch.verification.core.config.general.GlobalConfig
import com.sinch.verification.core.internal.error.VerificationException

/**
 * v2 seamless strategy. Opens the carrier app url, then awaits the App Link callback and
 * extracts the operator token from it.
 * @param globalConfig Shared SDK configuration used to launch the carrier app.
 * @param appInfoJwt Signed JWT containing application information, appended to the app url.
 * @param appInfoJwtQueryParameterName Query parameter name used to pass [appInfoJwt] in the url.
 * @param appCallbackQueryParameterName Query parameter name in the callback url carrying the operator token.
 * @param onLocalFailure Invoked when the flow fails locally; the caller owns failure reporting.
 * @param onLocalSuccess Invoked with the operator token; the caller owns reporting it.
 */
class AppUrlVerificationStrategy(
    private val globalConfig: GlobalConfig,
    private val appInfoJwt: String?,
    private val appInfoJwtQueryParameterName: String?,
    private val appCallbackQueryParameterName: String?,
    private val onLocalFailure: (Throwable) -> Unit,
    private val onLocalSuccess: (String) -> Unit
) : SeamlessVerificationStrategy {

    private val logger = logger()

    override fun start(payload: String) {
        if (appInfoJwt == null || appInfoJwtQueryParameterName == null || appCallbackQueryParameterName == null) {
            failLocally(
                VerificationException(
                    "androidAppUrl flow is missing mandatory fields: " +
                        "appInfoJwt, appInfoJwtQueryParameterName, appCallbackQueryParameterName"
                )
            )
            return
        }
        val urlToOpen = Uri.parse(payload).buildUpon()
            .appendQueryParameter(appInfoJwtQueryParameterName, appInfoJwt)
            .build()
        val intent = Intent(ACTION_VIEW, urlToOpen).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            logger.info("Starting carrier app for seamless verification with url: $urlToOpen")
            globalConfig.context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            val error = VerificationException("No app found to handle androidAppUrl")
            error.initCause(e)
            failLocally(error)
        }
    }

    override fun handleCallback(callbackUrl: String) {
        val callbackParameterName = appCallbackQueryParameterName
        if (callbackParameterName == null) {
            failLocally(
                VerificationException(
                    "Missing callback query parameter name for androidAppUrl callback. " +
                        "Ensure verify() is called on the same verification instance that initiated the App URL flow."
                )
            )
            return
        }
        val callbackUri = Uri.parse(callbackUrl)
        val token = callbackUri.getQueryParameter(callbackParameterName)
        if (token != null) {
            logger.debug("Operator token received from the carrier app callback")
            onLocalSuccess(token)
            return
        }
        val callbackError = callbackUri.getQueryParameter("error")
        if (callbackError != null) {
            val errorDescription = callbackUri.getQueryParameter("error_description")
            failLocally(
                VerificationException(
                    buildString {
                        append("Carrier app callback failed: ")
                        append(callbackError)
                        if (!errorDescription.isNullOrBlank()) {
                            append(" - ")
                            append(errorDescription)
                        }
                    }
                )
            )
            return
        }
        failLocally(VerificationException("Callback URL missing parameter '$callbackParameterName'"))
    }

    override fun stop() {}

    private fun failLocally(error: VerificationException) {
        onLocalFailure(error)
    }
}
