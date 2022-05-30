package com.sinch.sinchverification.view

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import com.sinch.logging.logger
import com.sinch.sinchverification.R
import com.sinch.sinchverification.VerificationSampleApp
import com.sinch.sinchverification.utils.logoverlay.LogOverlay
import com.sinch.verification.core.VerificationInitData
import com.sinch.verification.core.internal.VerificationMethodType
import com.sinch.verification.core.verification.VerificationLanguage
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        const val PERMISSION_REQUEST_CODE = 5
        const val VERIFICATION_DIALOG_TAG = "ver_dialog"
    }

    private val logger = logger()
    private val mainThreadHandler = Handler()
    private val myApplication: VerificationSampleApp
        get() =
            application as VerificationSampleApp

    private val buttonToMethodMap by lazy {
        mapOf(
            smsButton.id to VerificationMethodType.SMS,
            flashcallButton.id to VerificationMethodType.FLASHCALL,
            calloutButton.id to VerificationMethodType.CALLOUT,
            seamlessButton.id to VerificationMethodType.SEAMLESS,
            autoButton.id to VerificationMethodType.AUTO
        )
    }

    private val intervalTestingRunnable = object : Runnable {
        override fun run() {
            if (checkFields(autoCloseVerificationDialog = true)) {
                val nextVerificationDelayMS: Long = intervalTestingMinutes * 60 * 1000;
                mainThreadHandler.postDelayed(this, nextVerificationDelayMS)
                startCountDownTimerForIntervalVerification(nextVerificationDelayMS)
                logger.debug("Next interval testing scheduled in $intervalTestingMinutes minutes from now")
            } else {
                setIntervalTestingUI(false)
                logger.error("Did not schedule next verification call as checking fields failed")
            }
        }
    }

    private var countDownTimer: CountDownTimer? = null

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

    private val intervalTestingMinutes: Long
        get() {
            return try {
                intervalInputEditText.text.toString().toLong()
            } catch (e: NumberFormatException) {
                logger.error(
                    "Error while parsing input interval in minutes returning default (3 minutes)",
                    e
                )
                3
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initButton.setOnClickListener {
            ActivityCompat.requestPermissions(this, requestedPermissions, PERMISSION_REQUEST_CODE)
        }
        intervalTestingButton.setOnClickListener {
            mainThreadHandler.post(intervalTestingRunnable)
            setIntervalTestingUI(true)

        }
        intervalTestingButtonStop.setOnClickListener {
            setIntervalTestingUI(false)
            mainThreadHandler.removeCallbacks(intervalTestingRunnable)
        }
        optionalConfigButton.setOnClickListener {
            toggleOptionalConfig()
        }
        phoneInput.editText?.addTextChangedListener {
            phoneInput.error = null
        }
        toggleOptionalConfig()
    }

    override fun onStart() {
        super.onStart()
        LogOverlay.show()
    }

    override fun onStop() {
        super.onStop()
        LogOverlay.hide()
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            (R.id.settingsScreen) -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            else -> false
        }
    }

    private fun checkFields(autoCloseVerificationDialog: Boolean = false): Boolean {
        return when {
            phoneInput.editText?.text.isNullOrEmpty() -> {
                phoneInput.error = getString(R.string.phoneEmptyError)
                false
            }
            else -> {
                dismissVerificationDialogIfShown()
                VerificationDialog.newInstance(initData, autoCloseVerificationDialog)
                    .apply {
                        show(supportFragmentManager, VERIFICATION_DIALOG_TAG)
                    }
                true
            }
        }
    }

    private fun toggleOptionalConfig() {
        val shouldHide = optionalConfigLayout.children.first().isVisible
        optionalConfigButton.setText(if (shouldHide) R.string.show else R.string.hide)
        optionalConfigLayout.children.forEach {
            it.isVisible = !shouldHide
        }
    }

    private fun dismissVerificationDialogIfShown() {
        val currentVerificationDialogFragment =
            supportFragmentManager.findFragmentByTag(VERIFICATION_DIALOG_TAG)
        (currentVerificationDialogFragment as? DialogFragment)?.dismiss()
    }

    private fun setIntervalTestingUI(isOngoing: Boolean) {
        intervalTestingButton.isEnabled = !isOngoing
        intervalTestingButtonStop.isEnabled = isOngoing
        nextVerificationCallText.isVisible = isOngoing
    }

    private fun startCountDownTimerForIntervalVerification(countDownMs: Long) {
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(countDownMs, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                nextVerificationCallText.text = String.format(
                    getString(R.string.nextVerificationTemplate),
                    millisUntilFinished / 1000
                )
            }

            override fun onFinish() {}

        }.start()
    }

    private fun String.toLocaleList() = split(",")
        .filter { it.contains("-") }
        .map { it.split("-") }
        .map { VerificationLanguage(it[0], it[1]) }

}
