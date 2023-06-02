package com.sinch.sinchverification.view

import android.Manifest
import android.content.Intent
import android.os.Build
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
import com.sinch.sinchverification.databinding.ActivityMainBinding
import com.sinch.sinchverification.utils.logoverlay.LogOverlay
import com.sinch.verification.core.VerificationInitData
import com.sinch.verification.core.internal.VerificationMethodType
import com.sinch.verification.core.verification.VerificationLanguage

class MainActivity : AppCompatActivity() {

    companion object {
        const val PERMISSION_REQUEST_CODE = 5
        const val VERIFICATION_DIALOG_TAG = "ver_dialog"
    }

    private lateinit var binding: ActivityMainBinding

    private val logger = logger()
    private val mainThreadHandler = Handler()
    private val myApplication: VerificationSampleApp
        get() =
            application as VerificationSampleApp

    private val buttonToMethodMap by lazy {
        with(binding) {
            mapOf(
                smsButton.id to VerificationMethodType.SMS,
                flashcallButton.id to VerificationMethodType.FLASHCALL,
                calloutButton.id to VerificationMethodType.CALLOUT,
                seamlessButton.id to VerificationMethodType.SEAMLESS,
                autoButton.id to VerificationMethodType.AUTO
            )
        }
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
                usedMethod = buttonToMethodMap[binding.methodToggle.checkedButtonId]
                    ?: VerificationMethodType.SMS,
                number = binding.phoneInput.editText?.text.toString(),
                custom = binding.customInput.editText?.text.toString(),
                reference = binding.referenceInput.editText?.text.toString(),
                honourEarlyReject = binding.honoursEarlyCheckbox.isChecked,
                acceptedLanguages = binding.acceptedLanguagesInput.editText?.text.toString().toLocaleList()
            )

    private val selectedVerificationMethod: VerificationMethodType
        get() = buttonToMethodMap[binding.methodToggle.checkedButtonId]
            ?: VerificationMethodType.SMS

    private val requestedPermissions: Array<String>
        get() {
            var optionalPerms = arrayOf(
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                optionalPerms += Manifest.permission.READ_PHONE_NUMBERS
            }
            return if (selectedVerificationMethod == VerificationMethodType.FLASHCALL || selectedVerificationMethod == VerificationMethodType.AUTO) {
                optionalPerms + Manifest.permission.READ_CALL_LOG
            } else {
                optionalPerms
            }
        }

    private val intervalTestingMinutes: Long
        get() {
            return try {
                binding.intervalInputEditText.text.toString().toLong()
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
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.initButton.setOnClickListener {
            ActivityCompat.requestPermissions(this, requestedPermissions, PERMISSION_REQUEST_CODE)
        }
        binding.intervalTestingButton.setOnClickListener {
            mainThreadHandler.post(intervalTestingRunnable)
            setIntervalTestingUI(true)

        }
        binding.intervalTestingButtonStop.setOnClickListener {
            setIntervalTestingUI(false)
            mainThreadHandler.removeCallbacks(intervalTestingRunnable)
        }
        binding.optionalConfigButton.setOnClickListener {
            toggleOptionalConfig()
        }
        binding.phoneInput.editText?.addTextChangedListener {
            binding.phoneInput.error = null
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
            binding.phoneInput.editText?.text.isNullOrEmpty() -> {
                binding.phoneInput.error = getString(R.string.phoneEmptyError)
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
        val shouldHide = binding.optionalConfigLayout.children.first().isVisible
        binding.optionalConfigButton.setText(if (shouldHide) R.string.show else R.string.hide)
        binding.optionalConfigLayout.children.forEach {
            it.isVisible = !shouldHide
        }
    }

    private fun dismissVerificationDialogIfShown() {
        val currentVerificationDialogFragment =
            supportFragmentManager.findFragmentByTag(VERIFICATION_DIALOG_TAG)
        (currentVerificationDialogFragment as? DialogFragment)?.dismiss()
    }

    private fun setIntervalTestingUI(isOngoing: Boolean) {
        with(binding) {
            intervalTestingButton.isEnabled = !isOngoing
            intervalTestingButtonStop.isEnabled = isOngoing
            nextVerificationCallText.isVisible = isOngoing
        }
    }

    private fun startCountDownTimerForIntervalVerification(countDownMs: Long) {
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(countDownMs, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                binding.nextVerificationCallText.text = String.format(
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
