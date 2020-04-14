package com.sinch.verificationsamplejava;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.flipper.plugins.network.FlipperOkhttpInterceptor;
import com.sinch.smsverification.SmsVerificationMethod;
import com.sinch.smsverification.config.SmsVerificationConfig;
import com.sinch.smsverification.initialization.SmsInitiationResponseData;
import com.sinch.verificationcore.auth.AppKeyAuthorizationMethod;
import com.sinch.verificationcore.config.general.GlobalConfig;
import com.sinch.verificationcore.config.general.SinchGlobalConfig;
import com.sinch.verificationcore.initiation.response.InitiationListener;
import com.sinch.verificationcore.internal.Verification;
import com.sinch.verificationcore.verification.response.VerificationListener;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    //TODO check permissions

    private static final String TAG = "VerificationMethod";

    private Button initButton;
    private Button verifyButton;
    private EditText editText;

    private GlobalConfig globalConfig;
    private SmsVerificationConfig smsVerificationConfig;
    private InitiationListener<SmsInitiationResponseData> initiationListener;
    private VerificationListener verificationListener;

    private Verification smsVerificationMethod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initVerification();
        initButton = findViewById(R.id.initButton);
        verifyButton = findViewById(R.id.verifyButton);
        editText = findViewById(R.id.editText);
        initButton.setOnClickListener(v -> smsVerificationMethod.initiate());
        verifyButton.setOnClickListener(v -> smsVerificationMethod.verify(editText.getText().toString()));
    }

    private void initVerification() {
        globalConfig = SinchGlobalConfig.Builder.getInstance()
                .applicationContext(getApplicationContext())
                .authorizationMethod(new AppKeyAuthorizationMethod("9e556452-e462-4006-aab0-8165ca04de66"))
                .apiHost("https://verificationapi-v1.sinch.com/")
                .interceptors(Collections.singletonList(new FlipperOkhttpInterceptor(getApp().networkFlipperPlugin)))
                .build();

        smsVerificationConfig = SmsVerificationConfig.Builder.getInstance()
                .globalConfig(globalConfig)
                .number("+48509873255")
                .appHash("3O5HNxhoSme")
                .build();

        initiationListener = new InitiationListener<SmsInitiationResponseData>() {
            @Override
            public void onInitiated(@NotNull SmsInitiationResponseData data) {
                Log.d(TAG, "onInitiated: " + data);
            }

            @Override
            public void onInitializationFailed(@NotNull Throwable t) {
                Log.d(TAG, "onInitializationFailed " + t);
            }
        };

        verificationListener = new VerificationListener() {
            @Override
            public void onVerified() {
                Log.d(TAG, "onVerified");
            }

            @Override
            public void onVerificationFailed(@NotNull Throwable t) {
                Log.d(TAG, "onInitializationFailed " + t);
            }
        };

        smsVerificationMethod = SmsVerificationMethod.Builder.getInstance()
                .config(smsVerificationConfig)
                .initializationListener(initiationListener)
                .verificationListener(verificationListener)
                .build();

    }

    private VerificationJavaSampleApp getApp() {
        return (VerificationJavaSampleApp) getApplication();
    }
}
