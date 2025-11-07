package com.sinch.sinchverification.view

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION
import android.view.View
import android.widget.RadioButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import com.sinch.sinchverification.BuildConfig
import com.sinch.sinchverification.R
import com.sinch.sinchverification.VerificationSampleApp
import com.sinch.sinchverification.databinding.ActivitySettingsBinding
import com.sinch.sinchverification.utils.AppConfig
import com.sinch.sinchverification.utils.defaultConfigs
import com.sinch.verificationcore.BuildConfig.SINCH_SDK_VERSION_NAME

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    private val myApplication: VerificationSampleApp
        get() =
            application as VerificationSampleApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        attachUpdateGlobalConfigListeners()
        buildRadioButtons()
        updateGlobalConfigPropertiesLayout()
        attachRadioGroupChangeListener()
        populateVersionText()
        if (myApplication.usedConfig.appKey.isEmpty()) {
            showInsertAppKeyPrompt()
        }
        if (!Settings.canDrawOverlays(this)) {
            startActivity(Intent(ACTION_MANAGE_OVERLAY_PERMISSION))
        }
    }

    private fun attachUpdateGlobalConfigListeners() {
        binding.updateBaseUrlButton.setOnClickListener {
            myApplication.updateCurrentConfigBaseURL(
                binding.baseURLInputLayoutEditText.text.toString()
            )
        }
        binding.updateAppKeyButton.setOnClickListener {
            myApplication.updateCurrentConfigKey(binding.appKeyInputLayoutEditText.text.toString())
        }
    }

    private fun attachRadioGroupChangeListener() {
        binding.envRadioGroup.setOnCheckedChangeListener { _, checkedItemId ->
            onEnvironmentRadioButtonChecked(checkedItemId)
        }
    }

    private fun buildRadioButtons() {
        (defaultConfigs.map { it.name } + listOf(AppConfig.CUSTOM_CONFIG_NAME)).forEach {
            binding.envRadioGroup.addView(RadioButton(this).apply {
                text = it
                tag = it
            })
        }
    }

    private fun updateGlobalConfigPropertiesLayout() {
        val currentAppConfig = myApplication.usedConfig
        val idOfButtonToCheck = binding.envRadioGroup.children.first { currentAppConfig.name == it.tag }.id

        with(binding) {
            appKeyInputLayoutEditText.setText(currentAppConfig.appKey)
            baseURLInputLayoutEditText.setText(currentAppConfig.environment)
            envRadioGroup.check(idOfButtonToCheck)
            listOf<View>(baseURLInputLayoutEditText, updateBaseUrlButton).forEach {
                it.isEnabled = (currentAppConfig.isCustom)
            }
        }
    }

    private fun onEnvironmentRadioButtonChecked(checkedButtonId: Int) {
        val updatedConfigName =
            binding.envRadioGroup.children.first { it.id == checkedButtonId }.tag.toString()
        myApplication.updateAppConfig(updatedConfigName)
        updateGlobalConfigPropertiesLayout()
    }

    private fun populateVersionText() {
        binding.versionTextView.text = getString(
            R.string.versionPlaceholder,
            BuildConfig.VERSION_NAME,
            SINCH_SDK_VERSION_NAME
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