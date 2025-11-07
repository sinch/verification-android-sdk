package com.sinch.sinchverification.view

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputLayout
import com.sinch.logging.logger
import com.sinch.sinchverification.R
import com.sinch.sinchverification.VerificationSampleApp
import com.sinch.sinchverification.databinding.DialogVerificationBinding
import com.sinch.sinchverification.utils.AppSignatureHelper
import com.sinch.sinchverification.utils.appenders.LogMessageEvent
import com.sinch.verification.all.BasicVerificationMethodBuilder
import com.sinch.verification.all.CommonVerificationInitializationParameters
import com.sinch.verification.all.auto.initialization.AutoInitializationResponseData
import com.sinch.verification.core.VerificationInitData
import com.sinch.verification.core.initiation.response.InitiationListener
import com.sinch.verification.core.initiation.response.InitiationResponseData
import com.sinch.verification.core.internal.Verification
import com.sinch.verification.core.internal.VerificationMethodType
import com.sinch.verification.core.verification.response.VerificationListener
import java.util.Locale
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class VerificationDialog : DialogFragment(), VerificationListener {

    companion object {
        private const val DATA_TAG = "data"
        private const val AUTO_CLOSE_TAG = "auto_close"
        fun newInstance(initData: VerificationInitData, autoClose: Boolean = false) =
            VerificationDialog().apply {
                arguments = Bundle().apply {
                    putParcelable(DATA_TAG, initData)
                    putBoolean(AUTO_CLOSE_TAG, autoClose)
                }
            }
    }

    private var _binding: DialogVerificationBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    protected val logger = logger()
    private val app: VerificationSampleApp get() = activity?.application as VerificationSampleApp
    private val initData by lazy {
        arguments?.get(DATA_TAG) as VerificationInitData
    }

    private val autoClose by lazy {
        arguments?.getBoolean(AUTO_CLOSE_TAG, false) ?: false
    }

    private val initListener = object : InitiationListener<InitiationResponseData> {
        override fun onInitializationFailed(t: Throwable) {
            logger.error("onInitializationFailed callback executed with", t)
            showErrorWithMessage(t.message.orEmpty())
        }

        override fun onInitiated(data: InitiationResponseData) {
            logger.debug("onInitiated callback executed with initiation data: $data")
            if (data is AutoInitializationResponseData) {
                adjustVisibilityIfAutoMethodInitiated(data)
            }
        }
    }

    private val inputToMethodMap: Map<TextInputLayout, VerificationMethodType> by lazy {
        with(binding) {
            mapOf(
                smsVerificationCodeInput to VerificationMethodType.SMS,
                flashcallVerificationCodeInput to VerificationMethodType.FLASHCALL,
                calloutVerificationCodeInput to VerificationMethodType.CALLOUT
            )
        }
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
        _binding = DialogVerificationBinding.inflate(inflater, container, false)
        return binding.root
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
        binding.verifyButton.setOnClickListener {
            verification.verify(
                inputWithTypedCode?.editText?.text.toString(),
                inputToMethodMap[inputWithTypedCode]
            )
        }
        binding.quitButton.setOnClickListener {
            verification.stop()
            dismiss()
        }
        showInputsForMethod(initData.usedMethod)
        binding.verifyButton.isVisible = initData.usedMethod.allowsManualVerification
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: LogMessageEvent.Info) {
        appendLoggerText(event.msg)
    }

    override fun onVerified() {
        logger.debug("onVerified called")
        binding.progressBar.hide()
        binding.messageText.apply {
            setTextColor(ContextCompat.getColor(app, R.color.green))
            text = getString(R.string.successfullyVerified)
            binding.quitButton.text = getString(R.string.close)
            inputToMethodMap.keys.forEach { it.visibility = View.GONE }
            binding.verifyButton.visibility = View.GONE
        }
        if (autoClose) {
            Handler().postDelayed({ dismiss() }, 2000)
        }
    }

    override fun onVerificationFailed(t: Throwable) {
        logger.error("onVerificationFailed called", t)
        if (initData.usedMethod == VerificationMethodType.AUTO) {
            appendLoggerText(t.message ?: "Verification error")
        } else {
            showErrorWithMessage(t.message.orEmpty())
        }
        if (autoClose) {
            dismiss()
        }
    }

    private fun appendLoggerText(txt: String) {
        val initialText = if (binding.loggerText.text.isNullOrBlank()) "" else "${binding.loggerText.text}\n"
        binding.loggerText.text = "$initialText${txt}"
        binding.debugScrollView.post {
            binding.debugScrollView.fullScroll(View.FOCUS_DOWN)
        }
    }

    private fun showErrorWithMessage(text: String) {
        binding.progressBar.hide()
        binding.messageText.apply {
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
        with(binding) {
            smsVerificationCodeInput.isVisible = data.smsDetails != null
            flashcallVerificationCodeInput.isVisible = data.flashcallDetails != null
            calloutVerificationCodeInput.isVisible = data.calloutDetails != null
        }
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