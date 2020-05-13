package com.sinch.verification.sample;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.sinch.smsverification.SmsVerificationMethod;
import com.sinch.smsverification.config.SmsVerificationConfig;
import com.sinch.smsverification.initialization.SmsInitiationResponseData;
import com.sinch.verification.flashcall.FlashCallVerificationMethod;
import com.sinch.verification.flashcall.config.FlashCallVerificationConfig;
import com.sinch.verification.flashcall.initialization.FlashCallInitializationResponseData;
import com.sinch.verificationcore.auth.AppKeyAuthorizationMethod;
import com.sinch.verificationcore.config.general.GlobalConfig;
import com.sinch.verificationcore.config.general.SinchGlobalConfig;
import com.sinch.verificationcore.initiation.response.InitiationListener;
import com.sinch.verificationcore.internal.Verification;
import com.sinch.verificationcore.internal.error.CodeInterceptionException;
import com.sinch.verificationcore.verification.response.VerificationListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class VerificationActivity extends Activity implements ActivityCompat.OnRequestPermissionsResultCallback, VerificationListener {

    private static final String TAG = "VerificationActivity";

    private static final String APPLICATION_KEY = "9e556452-e462-4006-aab0-8165ca04de66";
    private static final String[] SMS_PERMISSIONS = {Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE};
    private static final String[] FLASHCALL_PERMISSIONS = {Manifest.permission.INTERNET,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.ACCESS_NETWORK_STATE};
    private static String APPLICATION_HASH;

    private boolean mIsSmsVerification;
    private boolean mShouldFallback = true;
    private boolean mIsVerified;
    private String mPhoneNumber;
    private Verification mVerification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        injectAppHash();
        Intent intent = getIntent();
        if (intent != null) {
            mPhoneNumber = intent.getStringExtra(MainActivity.INTENT_PHONENUMBER);
            final String method = intent.getStringExtra(MainActivity.INTENT_METHOD);
            mIsSmsVerification = method.equalsIgnoreCase(MainActivity.SMS);
            TextView phoneText = (TextView) findViewById(R.id.numberText);
            phoneText.setText(mPhoneNumber);

            requestPermissions();
        } else {
            Log.e(TAG, "The provided intent is null.");
        }
    }

    private void injectAppHash() {
        APPLICATION_HASH = new AppSignatureHelper(this).getAppSignatures().get(0);
    }

    private void requestPermissions() {
        List<String> missingPermissions;
        String methodText;

        if (mIsSmsVerification) {
            missingPermissions = getMissingPermissions(SMS_PERMISSIONS);
            methodText = "SMS";
        } else {
            missingPermissions = getMissingPermissions(FLASHCALL_PERMISSIONS);
            methodText = "calls";
        }

        if (missingPermissions.isEmpty()) {
            createVerification();
        } else {
            if (needPermissionsRationale(missingPermissions)) {
                Toast.makeText(this, "This application needs permissions to read your " + methodText + " to automatically verify your "
                        + "phone, you may disable the permissions once you have been verified.", Toast.LENGTH_LONG)
                        .show();
            }
            ActivityCompat.requestPermissions(this,
                    missingPermissions.toArray(new String[missingPermissions.size()]),
                    0);
        }
    }

    private void createVerification() {
        showProgress();

        GlobalConfig globalConfig = SinchGlobalConfig.Builder.getInstance()
                .applicationContext(getApplicationContext())
                .authorizationMethod(new AppKeyAuthorizationMethod(APPLICATION_KEY))
                .apiHost("https://verificationapi-v1.sinch.com/")
                .build();

        if (mIsSmsVerification) {
            createSmsVerification(globalConfig);
        } else {
            createFlashCallVerification(globalConfig);
        }
        mVerification.initiate();

    }

    private void createSmsVerification(GlobalConfig globalConfig) {
        SmsVerificationConfig smsVerificationConfig = SmsVerificationConfig.Builder.getInstance()
                .globalConfig(globalConfig)
                .number(mPhoneNumber)
                .appHash(APPLICATION_HASH)
                .build();

        mVerification = SmsVerificationMethod.Builder.getInstance()
                .config(smsVerificationConfig)
                .initializationListener(new MySmsInitListener())
                .verificationListener(this)
                .build();
    }

    private void createFlashCallVerification(GlobalConfig globalConfig) {
        FlashCallVerificationConfig flashCallVerificationConfig = FlashCallVerificationConfig.Builder.getInstance()
                .globalConfig(globalConfig)
                .number(mPhoneNumber)
                .build();

        mVerification = FlashCallVerificationMethod.Builder.getInstance()
                .config(flashCallVerificationConfig)
                .initializationListener(new MyFlashCallInitListener())
                .verificationListener(this)
                .build();
    }

    private boolean needPermissionsRationale(List<String> permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // Proceed with verification after requesting permissions.
        // If the verification SDK fails to intercept the code automatically due to missing permissions,
        // the VerificationListener.onVerificationFailed(1) method will be executed with an instance of
        // CodeInterceptionException. In this case it is still possible to proceed with verification
        // by asking the user to enter the code manually.
        createVerification();
    }

    private List<String> getMissingPermissions(String[] requiredPermissions) {
        List<String> missingPermissions = new ArrayList<>();
        for (String permission : requiredPermissions) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), permission)
                    != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }
        return missingPermissions;
    }

    public void onSubmitClicked(View view) {
        String code = ((EditText) findViewById(R.id.inputCode)).getText().toString();
        if (!code.isEmpty()) {
            if (mVerification != null) {
                mVerification.verify(code);
                showProgress();
                TextView messageText = (TextView) findViewById(R.id.textView);
                messageText.setText("Verification in progress");
                enableInputField(false);
            }
        }
    }

    private void enableInputField(boolean enable) {
        View container = findViewById(R.id.inputContainer);
        if (enable) {
            TextView hintText = (TextView) findViewById(R.id.enterToken);
            hintText.setText(mIsSmsVerification ? R.string.sms_enter_code : R.string.flashcall_enter_cli);
            container.setVisibility(View.VISIBLE);
            EditText input = (EditText) findViewById(R.id.inputCode);
            input.requestFocus();
        } else {
            container.setVisibility(View.GONE);
        }
    }

    private void hideProgressAndShowMessage(int message) {
        hideProgress();
        TextView messageText = (TextView) findViewById(R.id.textView);
        messageText.setText(message);
    }

    private void hideProgress() {
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressIndicator);
        progressBar.setVisibility(View.INVISIBLE);
        TextView progressText = (TextView) findViewById(R.id.progressText);
        progressText.setVisibility(View.INVISIBLE);
    }

    private void showProgress() {
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressIndicator);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void showCompleted() {
        ImageView checkMark = (ImageView) findViewById(R.id.checkmarkImage);
        checkMark.setVisibility(View.VISIBLE);
    }

    @Override
    public void onVerified() {
        mIsVerified = true;
        Log.d(TAG, "Verified!");
        hideProgressAndShowMessage(R.string.verified);
        showCompleted();
    }

    @Override
    public void onVerificationFailed(@NotNull Throwable t) {
        if (mIsVerified) {
            return;
        }

        Log.e(TAG, "Verification failed: " + t.getMessage());
        if (t instanceof CodeInterceptionException) {
            // Automatic code interception failed, probably due to missing permissions.
            // Let the user try and enter the code manually.
            hideProgress();
        } else {
            hideProgressAndShowMessage(R.string.failed);
        }
        enableInputField(true);
    }

    private class MySmsInitListener implements InitiationListener<SmsInitiationResponseData> {

        @Override
        public void onInitiated(@NotNull SmsInitiationResponseData data) {
            Log.d(TAG, "Data is" + data);
        }

        @Override
        public void onInitializationFailed(@NotNull Throwable t) {
            Log.e(TAG, "Verification initialization failed: " + t.getMessage());
            hideProgressAndShowMessage(R.string.failed);
        }
    }

    private class MyFlashCallInitListener implements InitiationListener<FlashCallInitializationResponseData> {

        @Override
        public void onInitiated(@NotNull FlashCallInitializationResponseData data) {
            Log.d(TAG, "Data is" + data);
        }

        @Override
        public void onInitializationFailed(@NotNull Throwable t) {
            Log.e(TAG, "Verification initialization failed: " + t.getMessage());
            hideProgressAndShowMessage(R.string.failed);
        }
    }

}
