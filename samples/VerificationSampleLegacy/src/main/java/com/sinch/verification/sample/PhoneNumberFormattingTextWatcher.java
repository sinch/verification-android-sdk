package com.sinch.verification.sample;

import android.annotation.SuppressLint;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

/**
 * This is a shim implementation to ensure that the same public classes are available in for the
 * 'minimal' flavor build variant as in the 'normal' flavor.
 * <p>
 * WARNING: This class is only used to make it possible to compile the same sample app source
 * code against both flavors / build variants of the Sinch Verification SDK.
 * <p>
 * Compared to the "real" implementation of
 * {@link com.sinch.verification.sample.PhoneNumberFormattingTextWatcher}
 * that is made available in the 'normal' build flavor of the Sinch Verification SDK which
 * simplifies handling Android platform API level backwards compatibility issues, this shim only
 * forwards calls to {@link android.telephony .PhoneNumberFormattingTextWatcher}.
 */
@SuppressLint("NewApi")
@SuppressWarnings("unused")
public class PhoneNumberFormattingTextWatcher implements TextWatcher {

    private static final String TAG = PhoneNumberFormattingTextWatcher.class.getSimpleName();

    private static final String SHIM_WARNING = "You are using a shim implementation of " +
            "PhoneNumberFormattingTextWatcher. " +
            "This class is only available in the sample application and should _NOT_ be used in " +
            "production applications that are distributed to the Play Store";

    private android.telephony.PhoneNumberFormattingTextWatcher mDelegate;

    @SuppressLint("LongLogTag")
    public PhoneNumberFormattingTextWatcher(String countryIso) {
        mDelegate = new android.telephony.PhoneNumberFormattingTextWatcher(countryIso);
        Log.w(TAG, SHIM_WARNING);
    }

    @Override
    public void afterTextChanged(Editable s) {
        mDelegate.afterTextChanged(s);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        mDelegate.beforeTextChanged(s, start, count, after);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mDelegate.onTextChanged(s, start, before, count);
    }


}

