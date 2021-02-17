package com.sinch.sinchverification.view

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.sinch.sinchverification.*
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity(), GlobalConfigPropertiesUpdateListener {

    private val myApplication: VerificationSampleApp
        get() =
            application as VerificationSampleApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        myApplication.childGlobalConfigPropertiesUpdateListener = this
        attachUpdateGlobalConfigListeners()
        updateGlobalConfigPropertiesLayout()
        attachRadioGroupChangeListener()
        populateVersionText()
        if (myApplication.usedApplicationKey.isEmpty()) {
            showInsertAppKeyPrompt()
        }
    }

    override fun onBaseURLUpdated(baseURL: String, isCustom: Boolean) {
        baseURLInputLayoutEditText.isEnabled = isCustom
        updateBaseUrlButton.isEnabled = isCustom
        updateGlobalConfigPropertiesLayout()
    }

    override fun onAppKeyUpdated(appKey: String) {
        updateGlobalConfigPropertiesLayout()
    }

    private fun attachUpdateGlobalConfigListeners() {
        updateBaseUrlButton.setOnClickListener {
            myApplication.updateBaseUrlManually(
                baseURLInputLayoutEditText.text.toString()
            )
        }
        updateAppKeyButton.setOnClickListener {
            myApplication.updateAppKeyManually(appKeyInputLayoutEditText.text.toString())
        }
    }

    private fun attachRadioGroupChangeListener() {
        radioGroup.setOnCheckedChangeListener { _, checkedItemId ->
            onEnvironmentRadioButtonChecked(checkedItemId)
        }
    }

    private fun updateGlobalConfigPropertiesLayout() {
        appKeyInputLayoutEditText.setText(myApplication.usedApplicationKey)
        baseURLInputLayoutEditText.setText(myApplication.usedBaseUrl)
        radioGroup.check(
            when (myApplication.selectedEnvironment) {
                Environment.PRODUCTION -> R.id.productionApi
                Environment.APSE1 -> R.id.productionAPSE1
                Environment.EUC1 -> R.id.productionEUC1
                Environment.FTEST1 -> R.id.ftest1Api
                Environment.FTEST2 -> R.id.ftest2Api
                Environment.CUSTOM -> R.id.customItemId
            }
        )
        listOf<View>(baseURLInputLayoutEditText, updateBaseUrlButton).forEach {
            it.isEnabled = (myApplication.selectedEnvironment == Environment.CUSTOM)
        }
    }

    private fun onEnvironmentRadioButtonChecked(checkedButtonId: Int) {
        val newEnvironment = when (checkedButtonId) {
            R.id.productionApi -> Environment.PRODUCTION
            R.id.productionAPSE1 -> Environment.APSE1
            R.id.productionEUC1 -> Environment.EUC1
            R.id.ftest1Api -> Environment.FTEST1
            R.id.ftest2Api -> Environment.FTEST2
            R.id.customItemId -> Environment.CUSTOM
            else -> throw RuntimeException("RadioButton with $checkedButtonId not handled")
        }
        myApplication.updateSelectedEnvironment(newEnvironment)
        updateGlobalConfigPropertiesLayout()
    }

    private fun populateVersionText() {
        versionTextView.text = getString(
            R.string.versionPlaceholder,
            BuildConfig.VERSION_NAME,
            com.sinch.verification.all.BuildConfig.VERSION_NAME
        )
    }

    private fun showInsertAppKeyPrompt() {
        AlertDialog.Builder(this)
            .setMessage(R.string.appKeyMissing)
            .setCancelable(false)
            .setPositiveButton(R.string.ok, null)
            .show()
    }

}