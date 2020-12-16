package com.sinch.sinchverification

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputLayout
import com.sinch.verification.all.BasicVerificationMethodBuilder
import com.sinch.verification.all.CommonVerificationInitializationParameters
import com.sinch.verification.all.auto.initialization.AutoInitializationResponseData
import com.sinch.verification.core.VerificationInitData
import com.sinch.verification.core.initiation.response.InitiationListener
import com.sinch.verification.core.initiation.response.InitiationResponseData
import com.sinch.verification.core.internal.Verification
import com.sinch.verification.core.internal.VerificationMethodType
import com.sinch.verification.core.verification.VerificationEvent
import com.sinch.verification.core.verification.response.VerificationListener
import kotlinx.android.synthetic.main.dialog_verification.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
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

        override fun onInitiated(data: InitiationResponseData) {
            if (data is AutoInitializationResponseData) {
                adjustVisibilityIfAutoMethodInitiated(data)
            }
        }
    }

    private val inputToMethodMap: Map<TextInputLayout, VerificationMethodType> by lazy {
        mapOf(
            smsVerificationCodeInput to VerificationMethodType.SMS,
            flashcallVerificationCodeInput to VerificationMethodType.FLASHCALL,
            calloutVerificationCodeInput to VerificationMethodType.CALLOUT
        )
    }

    private val inputWithTypedCode: TextInputLayout?
        get() =
            inputToMethodMap.entries.firstOrNull { !it.key.editText?.text.isNullOrEmpty() }?.key


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

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
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
        addInputTextWatchers()
        verifyButton.setOnClickListener {
            verification.verify(
                inputWithTypedCode?.editText?.text.toString(),
                inputToMethodMap[inputWithTypedCode]
            )
        }
        quitButton.setOnClickListener {
            verification.stop()
            dismiss()
        }
        showInputsForMethod(initData.usedMethod)
        verifyButton.isVisible = initData.usedMethod.allowsManualVerification
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: LogMessageEvent.Info) {
        appendLoggerText(event.msg)
    }

    override fun onVerified() {
        progressBar.hide()
        messageText.apply {
            setTextColor(ContextCompat.getColor(app, R.color.green))
            text = getString(R.string.successfullyVerified)
            quitButton.text = getString(R.string.close)
            inputToMethodMap.keys.forEach { it.visibility = View.GONE }
            verifyButton.visibility = View.GONE
        }
    }

    override fun onVerificationFailed(t: Throwable) {
        if (initData.usedMethod == VerificationMethodType.AUTO) {
            appendLoggerText(t.message ?: "Verification error")
        } else {
            showErrorWithMessage(t.message.orEmpty())
        }
    }

    override fun onVerificationEvent(event: VerificationEvent) {
        appendLoggerText("Verification event received $event")
    }

    private fun appendLoggerText(txt: String) {
        val initialText = if (loggerText.text.isNullOrBlank()) "" else "${loggerText.text}\n"
        loggerText.text = "$initialText${txt}"
        debugScrollView.post {
            debugScrollView.fullScroll(View.FOCUS_DOWN)
        }
    }

    private fun showErrorWithMessage(text: String) {
        progressBar.hide()
        messageText.apply {
            setTextColor(ContextCompat.getColor(app, R.color.red))
            this.text =
                String.format(Locale.US, getString(R.string.verificationFailedPlaceholder), text)
        }
    }

    private fun showInputsForMethod(method: VerificationMethodType) {
        if (method == VerificationMethodType.AUTO) {
            return //ALL visible for now
        }
        inputToMethodMap.entries.forEach {
            it.key.isVisible = it.value == method
        }
    }

    private fun adjustVisibilityIfAutoMethodInitiated(data: AutoInitializationResponseData) {
        smsVerificationCodeInput.isVisible = data.smsDetails != null
        flashcallVerificationCodeInput.isVisible = data.flashcallDetails != null
        calloutVerificationCodeInput.isVisible = data.calloutDetails != null
        inputToMethodMap.keys.first { it.isVisible }.let { it.isSelected = true }
    }

    private fun addInputTextWatchers() {
        inputToMethodMap.keys.forEach { inputLayout ->
            inputLayout.editText?.addTextChangedListener {
                clearInputs(except = inputLayout)
            }
        }
    }

    private fun clearInputs(except: TextInputLayout) {
        inputToMethodMap.keys.forEach {
            if (it != except) {
                it.editText?.text?.clear()
            }
        }
    }

}