package com.sinch.sinchverification.view

import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import com.sinch.sinchverification.BuildConfig
import com.sinch.sinchverification.R
import com.sinch.sinchverification.VerificationSampleApp
import com.sinch.sinchverification.utils.AppConfig
import com.sinch.sinchverification.utils.defaultConfigs
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {

    private val myApplication: VerificationSampleApp
        get() =
            application as VerificationSampleApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        attachUpdateGlobalConfigListeners()
        buildRadioButtons()
        updateGlobalConfigPropertiesLayout()
        attachRadioGroupChangeListener()
        populateVersionText()
        if (myApplication.usedConfig.appKey.isEmpty()) {
            showInsertAppKeyPrompt()
        }
    }

    private fun attachUpdateGlobalConfigListeners() {
        updateBaseUrlButton.setOnClickListener {
            myApplication.updateCurrentConfigBaseURL(
                baseURLInputLayoutEditText.text.toString()
            )
        }
        updateAppKeyButton.setOnClickListener {
            myApplication.updateCurrentConfigKey(appKeyInputLayoutEditText.text.toString())
        }
    }

    private fun attachRadioGroupChangeListener() {
        envRadioGroup.setOnCheckedChangeListener { _, checkedItemId ->
            onEnvironmentRadioButtonChecked(checkedItemId)
        }
    }

    private fun buildRadioButtons() {
        (defaultConfigs.map { it.name } + listOf(AppConfig.CUSTOM_CONFIG_NAME)).forEach {
            envRadioGroup.addView(RadioButton(this).apply {
                text = it
                tag = it
            })
        }
    }

    private fun updateGlobalConfigPropertiesLayout() {
        val currentAppConfig = myApplication.usedConfig
        val idOfButtonToCheck = envRadioGroup.children.first { currentAppConfig.name == it.tag }.id

        appKeyInputLayoutEditText.setText(currentAppConfig.appKey)
        baseURLInputLayoutEditText.setText(currentAppConfig.environment)
        envRadioGroup.check(idOfButtonToCheck)
        listOf<View>(baseURLInputLayoutEditText, updateBaseUrlButton).forEach {
            it.isEnabled = (currentAppConfig.isCustom)
        }
    }

    private fun onEnvironmentRadioButtonChecked(checkedButtonId: Int) {
        val updatedConfigName =
            envRadioGroup.children.first { it.id == checkedButtonId }.tag.toString()
        myApplication.updateAppConfig(updatedConfigName)
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