package com.sinch.verificationsamplejava;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.sinch.verification.core.VerificationInitData;
import com.sinch.verification.core.internal.VerificationMethodType;
import com.sinch.verification.core.verification.VerificationLanguage;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

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
        initButton.setOnClickListener(v -> checkFields());
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

    private void checkFields() {
        Editable phoneNumber = phoneEditText.getText();
        if (phoneNumber == null || phoneNumber.toString().isEmpty()) {
            phoneEditInput.setError(getString(R.string.phoneEmptyError));
        } else {
            VerificationDialog.newInstance(createInitDataFromInputs()).show(getSupportFragmentManager(), "dialog");
        }
    }

    private List<VerificationLanguage> splitAcceptLanguagesField(String source) {
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
}
