package com.sinch.verification.smssample

import android.Manifest
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.widget.addTextChangedListener
import com.sinch.verification.core.VerificationInitData
import com.sinch.verification.core.internal.VerificationMethodType
import com.sinch.verification.core.verification.VerificationLanguage
import kotlinx.android.synthetic.main.activity_main_sms.*

class MainActivity : AppCompatActivity() {

    companion object {
        const val PERMISSION_REQUEST_CODE = 5
    }

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
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.ACCESS_NETWORK_STATE
                ), PERMISSION_REQUEST_CODE
            )
        }
        phoneInput.editText?.addTextChangedListener {
            phoneInput.error = null
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        //We simply proceed with the verification
        checkFields()
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
