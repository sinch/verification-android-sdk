package com.sinch.verificationsamplejava;

import android.Manifest;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.sinch.verification.core.VerificationInitData;
import com.sinch.verification.core.internal.VerificationMethodType;
import com.sinch.verification.core.verification.VerificationLanguage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static int PERMISSION_REQUEST_CODE = 5;

    private Button initButton;
    private TextInputLayout phoneEditInput;
    private TextInputEditText phoneEditText;
    private TextInputEditText customEditText;
    private TextInputEditText referenceEditText;
    private TextInputEditText acceptedLanguagesEditText;
    private MaterialCheckBox honoursEarlyCheckbox;
    private MaterialButtonToggleGroup methodToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindViews();
        initButton.setOnClickListener(v -> requestPermissions());
        phoneEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                phoneEditInput.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //We simply proceed with the verification
        checkFields();
    }

    private void bindViews() {
        initButton = findViewById(R.id.initButton);
        phoneEditInput = findViewById(R.id.phoneInput);
        phoneEditText = findViewById(R.id.phoneInputEditText);
        methodToggle = findViewById(R.id.methodToggle);
        customEditText = findViewById(R.id.customInputEditText);
        referenceEditText = findViewById(R.id.referenceInputEditText);
        acceptedLanguagesEditText = findViewById(R.id.acceptedLanguagesInputEditText);
        honoursEarlyCheckbox = findViewById(R.id.honoursEarlyCheckbox);
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, getRequestedPermissions(), PERMISSION_REQUEST_CODE);
    }

    private void checkFields() {
        Editable phoneNumber = phoneEditText.getText();
        if (phoneNumber == null || phoneNumber.toString().isEmpty()) {
            phoneEditInput.setError(getString(R.string.phoneEmptyError));
        } else {
            VerificationDialog.newInstance(createInitDataFromInputs()).show(getSupportFragmentManager(), "dialog");
        }
    }

    private List<VerificationLanguage> splitAcceptLanguagesField(String source) {
        if (source == null || source.isEmpty()) {
            return Collections.emptyList();
        }
        String[] slices = source.split(",");
        List<VerificationLanguage> result = new ArrayList<>();
        for (String slice : slices) {
            if (slice.contains("-")) {
                String[] locale = slice.split("-");
                result.add(new VerificationLanguage(locale[0], locale[1], null));
            } else {
                throw new RuntimeException("Wrong accept-languages format. Sample app supports only [language-region] format e.g. es-ES, fr-CA");
            }
        }
        return result;
    }

    private VerificationInitData createInitDataFromInputs() {
        return new VerificationInitData(
                getCheckedVerificationMethod(),
                phoneEditText.getText().toString(),
                customEditText.getText().toString(),
                referenceEditText.getText().toString(),
                honoursEarlyCheckbox.isChecked(),
                splitAcceptLanguagesField(acceptedLanguagesEditText.getText().toString())
        );
    }

    private VerificationMethodType getCheckedVerificationMethod() {
        switch (methodToggle.getCheckedButtonId()) {
            case R.id.smsButton:
                return VerificationMethodType.SMS;
            case R.id.calloutButton:
                return VerificationMethodType.CALLOUT;
            case R.id.flashcallButton:
                return VerificationMethodType.FLASHCALL;
            case R.id.seamlessButton:
                return VerificationMethodType.SEAMLESS;
            default:
                throw new RuntimeException("No method for button" + methodToggle.getCheckedButtonId());
        }
    }

    private String[] getRequestedPermissions() {
        List<String> permissions = Arrays.asList(Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.READ_PHONE_STATE);
        if (methodToggle.getCheckedButtonId() == R.id.flashcallButton) {
            permissions.add(Manifest.permission.READ_CALL_LOG);
        }
        String[] permsArray = new String[permissions.size()];
        return permissions.toArray(permsArray);
    }
}
