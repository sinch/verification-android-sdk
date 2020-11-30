package com.sinch.verification.smssample

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.sinch.verification.core.VerificationInitData
import com.sinch.verification.core.internal.Verification
import com.sinch.verification.core.verification.response.VerificationListener
import com.sinch.verification.sms.SmsVerificationMethod
import com.sinch.verification.sms.config.SmsVerificationConfig
import com.sinch.verification.sms.initialization.SmsInitializationListener
import com.sinch.verification.sms.initialization.SmsInitiationResponseData
import kotlinx.android.synthetic.main.dialog_verification.*
import java.util.*

class VerificationDialog : DialogFragment(), VerificationListener {

    companion object {
        private const val DATA_TAG = "data"
        fun newInstance(initData: VerificationInitData) = VerificationDialog().apply {
            arguments = Bundle().apply { putParcelable(DATA_TAG, initData) }
        }
    }

    private val app: VerificationSampleApp get() = activity?.application as VerificationSampleApp
    private val initData by lazy {
        arguments?.get(DATA_TAG) as VerificationInitData
    }

    private val initListener = object : SmsInitializationListener {
        override fun onInitializationFailed(t: Throwable) {
            showErrorWithMessage(t.message.orEmpty())
        }

        override fun onInitiated(data: SmsInitiationResponseData) {}
    }

    private lateinit var verification: Verification

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            isCancelable = false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_verification, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //Normally we would implement all the logic inside View Model but for simplicity we will keep it here.
        verification = SmsVerificationMethod.Builder().config(
            SmsVerificationConfig.Builder().globalConfig(app.globalConfig)
                .number(initData.number)
                .appHash(AppSignatureHelper(app).appSignatures[0])
                .custom(initData.custom)
                .honourEarlyReject(initData.honourEarlyReject)
                .reference(initData.reference)
                .acceptedLanguages(initData.acceptedLanguages)
                .build()
        )
            .initializationListener(initListener)
            .verificationListener(this)
            .build()
            .also { it.initiate() }

        verifyButton.setOnClickListener {
            verification.verify(codeInput.editText?.text.toString())
        }
        quitButton.setOnClickListener {
            verification.stop()
            dismiss()
        }
    }

    override fun onVerified() {
        progressBar.hide()
        messageText.apply {
            setTextColor(ContextCompat.getColor(app, R.color.green))
            text = getString(R.string.successfullyVerified)
            quitButton.text = getString(R.string.close)
            codeInput.visibility = View.GONE
            verifyButton.visibility = View.GONE
        }
    }

    override fun onVerificationFailed(t: Throwable) {
        showErrorWithMessage(t.message.orEmpty())
    }

    private fun showErrorWithMessage(text: String) {
        progressBar.hide()
        messageText.apply {
            setTextColor(ContextCompat.getColor(app, R.color.red))
            this.text =
                String.format(Locale.US, getString(R.string.verificationFailedPlaceholder), text)
        }
    }

}