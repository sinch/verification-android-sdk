package com.sinch.sinchverification

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.sinch.verification.all.BasicVerificationMethodBuilder
import com.sinch.verification.all.CommonVerificationInitializationParameters
import com.sinch.verificationcore.VerificationInitData
import com.sinch.verificationcore.initiation.response.InitiationListener
import com.sinch.verificationcore.initiation.response.InitiationResponseData
import com.sinch.verificationcore.internal.Verification
import com.sinch.verificationcore.verification.response.VerificationListener
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

    private val initListener = object : InitiationListener<InitiationResponseData> {
        override fun onInitializationFailed(t: Throwable) {
            showErrorWithMessage(t.message.orEmpty())
        }

        override fun onInitiated(data: InitiationResponseData) {}
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
        verification = BasicVerificationMethodBuilder.createVerification(
            commonVerificationInitializationParameters = CommonVerificationInitializationParameters(
                globalConfig = app.globalConfig,
                verificationInitData = initData,
                initiationListener = initListener,
                verificationListener = this
            ),
            appHash = AppSignatureHelper(app).appSignatures[0]
        ).also { it.initiate() }
        verifyButton.setOnClickListener {
            verification.verify(codeInput.editText?.text.toString())
        }
        quitButton.setOnClickListener {
            verification.stop()
            dismiss()
        }
        listOf(codeInput, verifyButton).forEach {
            it.visibility =
                if (initData.usedMethod.allowsManualVerification) View.VISIBLE else View.GONE
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