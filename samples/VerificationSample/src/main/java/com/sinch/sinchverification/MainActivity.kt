package com.sinch.sinchverification

import android.Manifest
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.widget.addTextChangedListener
import com.sinch.verification.core.VerificationInitData
import com.sinch.verification.core.internal.VerificationMethodType
import com.sinch.verification.core.verification.VerificationLanguage
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        const val PERMISSION_REQUEST_CODE = 5
    }

    private val buttonToMethodMap by lazy {
        mapOf(
            smsButton.id to VerificationMethodType.SMS,
            flashcallButton.id to VerificationMethodType.FLASHCALL,
            calloutButton.id to VerificationMethodType.CALLOUT,
            seamlessButton.id to VerificationMethodType.SEAMLESS,
            autoButton.id to VerificationMethodType.AUTO
        )
    }

    private val initData: VerificationInitData
        get() =
            VerificationInitData(
                usedMethod = buttonToMethodMap[methodToggle.checkedButtonId]
                    ?: VerificationMethodType.SMS,
                number = phoneInput.editText?.text.toString(),
                custom = customInput.editText?.text.toString(),
                reference = referenceInput.editText?.text.toString(),
                honourEarlyReject = honoursEarlyCheckbox.isChecked,
                acceptedLanguages = acceptedLanguagesInput?.editText?.text.toString().toLocaleList()
            )

    private val selectedVerificationMethod: VerificationMethodType
        get() = buttonToMethodMap[methodToggle.checkedButtonId]
            ?: VerificationMethodType.SMS

    private val requestedPermissions: Array<String>
        get() {
            val optionalPerms = arrayOf(
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_NETWORK_STATE
            )
            return if (selectedVerificationMethod == VerificationMethodType.FLASHCALL || selectedVerificationMethod == VerificationMethodType.AUTO) {
                optionalPerms + Manifest.permission.READ_CALL_LOG
            } else {
                optionalPerms
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initButton.setOnClickListener {
            ActivityCompat.requestPermissions(this, requestedPermissions, PERMISSION_REQUEST_CODE)
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        (application as VerificationSampleApp).onDevelopmentOptionSelected(item)

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
