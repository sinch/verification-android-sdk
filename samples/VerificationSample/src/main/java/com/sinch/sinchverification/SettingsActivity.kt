package com.sinch.sinchverification

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
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
        appKeyInputLayoutEditText.setText(myApplication.appKey)
        baseURLInputLayoutEditText.setText(myApplication.apiBaseURL)
        radioGroup.check(
            when (myApplication.apiBaseURL) {
                BuildConfig.API_BASE_URL_PROD -> R.id.productionApi
                BuildConfig.API_BASE_URL_PROD_APSE1 -> R.id.productionAPSE1
                BuildConfig.API_BASE_URL_PROD_EUC1 -> R.id.productionEUC1
                BuildConfig.API_BASE_URL_FTEST1 -> R.id.ftest1Api
                BuildConfig.API_BASE_URL_FTEST2 -> R.id.ftest2Api
                else -> R.id.customItemId
            }
        )
        listOf<View>(baseURLInputLayoutEditText, updateBaseUrlButton).forEach {
            it.isEnabled = (radioGroup.checkedRadioButtonId == R.id.customItemId)
        }
    }

    private fun onEnvironmentRadioButtonChecked(checkedButtonId: Int) {
        val apiBaseURL = when (checkedButtonId) {
            R.id.productionApi -> BuildConfig.API_BASE_URL_PROD
            R.id.productionAPSE1 -> BuildConfig.API_BASE_URL_PROD_APSE1
            R.id.productionEUC1 -> BuildConfig.API_BASE_URL_PROD_EUC1
            R.id.ftest1Api -> BuildConfig.API_BASE_URL_FTEST1
            R.id.ftest2Api -> BuildConfig.API_BASE_URL_FTEST2
            R.id.customItemId -> ""
            else -> throw RuntimeException("RadioButton with $checkedButtonId not handled")
        }
        val appKey = when (checkedButtonId) {
            R.id.productionApi -> BuildConfig.APP_KEY_PROD
            R.id.productionAPSE1 -> BuildConfig.APP_KEY_PROD_APSE
            R.id.productionEUC1 -> BuildConfig.APP_KEY_PROD_EUC1
            R.id.ftest1Api -> BuildConfig.APP_KEY_FTEST1
            R.id.ftest2Api -> BuildConfig.APP_KEY_FTEST2
            R.id.customItemId -> ""
            else -> throw RuntimeException("Menu item with $checkedButtonId not handled")
        }
        myApplication.updateAppKeyManually(appKey)
        myApplication.updateBaseUrlManually(apiBaseURL)
        updateGlobalConfigPropertiesLayout()
    }

    private fun populateVersionText() {
        versionTextView.text = getString(
            R.string.versionPlaceholder,
            BuildConfig.VERSION_NAME,
            com.sinch.verification.all.BuildConfig.VERSION_NAME
        )
    }

}