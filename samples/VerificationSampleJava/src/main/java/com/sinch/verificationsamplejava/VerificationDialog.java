package com.sinch.verificationsamplejava;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.sinch.verification.all.BasicVerificationMethodBuilder;
import com.sinch.verification.all.CommonVerificationInitializationParameters;
import com.sinch.verification.core.VerificationInitData;
import com.sinch.verification.core.config.general.GlobalConfig;
import com.sinch.verification.core.initiation.response.InitiationListener;
import com.sinch.verification.core.initiation.response.InitiationResponseData;
import com.sinch.verification.core.internal.Verification;
import com.sinch.verification.core.internal.VerificationMethodType;
import com.sinch.verification.core.verification.response.VerificationListener;

import org.jetbrains.annotations.NotNull;

public class VerificationDialog extends DialogFragment implements InitiationListener<InitiationResponseData>, VerificationListener {

    private static final String DATA_TAG = "data";

    private Button verifyButton;
    private Button quitButton;
    private TextView messageTextView;
    private ContentLoadingProgressBar progressBar;
    private TextInputLayout codeInput;
    private TextInputEditText codeInputEditText;

    private Verification verification;

    static VerificationDialog newInstance(VerificationInitData initData) {
        VerificationDialog verificationDialog = new VerificationDialog();
        Bundle args = new Bundle();
        args.putParcelable(DATA_TAG, initData);
        verificationDialog.setArguments(args);
        return verificationDialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setCancelable(false);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.dialog_verification, container, false);
        verifyButton = root.findViewById(R.id.verifyButton);
        quitButton = root.findViewById(R.id.quitButton);
        messageTextView = root.findViewById(R.id.messageText);
        progressBar = root.findViewById(R.id.progressBar);
        codeInputEditText = root.findViewById(R.id.codeInputEditText);
        codeInput = root.findViewById(R.id.codeInput);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Normally we would implement all the logic inside View Model but for simplicity we will keep it here.
        verification = BasicVerificationMethodBuilder.createVerification(
                new CommonVerificationInitializationParameters(
                        getGlobalConfig(),
                        getInitDataFromBundle(),
                        this,
                        this
                ), new AppSignatureHelper(getContext()).getAppSignatures().get(0)
        );
        verification.initiate();
        quitButton.setOnClickListener(v -> {
            verification.stop();
            dismiss();
        });
        verifyButton.setOnClickListener(v -> {
            verification.verify(codeInputEditText.getText().toString());
        });
        adjustVisibilityOfManualVerificationField(codeInput);
        adjustVisibilityOfManualVerificationField(verifyButton);
    }

    @Override
    public void onInitiated(@NotNull InitiationResponseData data) {

    }

    @Override
    public void onInitializationFailed(@NotNull Throwable t) {
        showErrorWithMessage(t.getMessage());
    }

    @Override
    public void onVerified() {
        progressBar.hide();
        messageTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
        messageTextView.setText(R.string.successfullyVerified);
        quitButton.setText(getString(R.string.close));
        verifyButton.setVisibility(View.GONE);
        codeInputEditText.setVisibility(View.GONE);
    }

    @Override
    public void onVerificationFailed(@NotNull Throwable t) {
        showErrorWithMessage(t.getMessage());
    }

    private void showErrorWithMessage(String text) {
        progressBar.hide();
        messageTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
        messageTextView.setText(String.format(getString(R.string.verificationFailedPlaceholder), text));
    }

    private VerificationInitData getInitDataFromBundle() {
        return getArguments().getParcelable(DATA_TAG);
    }

    private GlobalConfig getGlobalConfig() {
        return ((VerificationJavaSampleApp) getActivity().getApplication()).getGlobalConfig();
    }

    private void adjustVisibilityOfManualVerificationField(View view) {
        VerificationMethodType usedMethod = getInitDataFromBundle().getUsedMethod();
        view.setVisibility(
                usedMethod.getAllowsManualVerification() ? View.VISIBLE : View.GONE
        );
    }
}
