package com.sinch.verification.smssample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.sinch.verification.core.VerificationInitData
import com.sinch.verification.core.internal.VerificationMethodType
import com.sinch.verification.core.verification.VerificationLanguage
import kotlinx.android.synthetic.main.activity_main_sms.*

class MainActivity : AppCompatActivity() {

    private val initData: VerificationInitData
        get() =
            VerificationInitData(
                usedMethod = VerificationMethodType.SMS,
                number = phoneInput.editText?.text.toString(),
                custom = customInput.editText?.text.toString(),
                reference = referenceInput.editText?.text.toString(),
                honourEarlyReject = honoursEarlyCheckbox.isChecked,
                acceptedLanguages = acceptedLanguagesInput?.editText?.text.toString().toLocaleList()
            )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_sms)
        initButton.setOnClickListener {
            checkFields()
        }
        phoneInput.editText?.addTextChangedListener {
            phoneInput.error = null
        }
    }

    private fun checkFields() {
        if (phoneInput.editText?.text.isNullOrEmpty()) {
            phoneInput.error = getString(R.string.phoneEmptyError)
        } else {
            VerificationDialog.newInstance(initData)
                .apply {
                    show(supportFragmentManager, "dialog")
                }
        }
    }

    private fun String.toLocaleList() = split(",")
        .filter { it.contains("-") }
        .map { it.split("-") }
        .map { VerificationLanguage(it[0], it[1]) }

}
