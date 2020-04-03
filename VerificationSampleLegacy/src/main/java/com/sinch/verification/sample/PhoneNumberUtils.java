package com.sinch.verification.sample;

import android.content.Context;
import android.util.Log;

import java.util.Locale;

/**
 * This is a shim implementation to ensure that the same public classes are available in for the
 * 'minimal' flavor build variant as in the 'normal' flavor.
 * <p>
 * WARNING: This class is only used to make it possible to compile the same sample app source
 * code against both flavors / build variants of the Sinch Verification SDK.
 * <p>
 * Compared to the "real" implementation of
 * {@link com.sinch.verification.PhoneNumberUtils}
 * that is made available in the 'normal' build flavor of the Sinch Verification SDK, this shim
 * implementation is a dummy only.
 */
public class PhoneNumberUtils {

    private static final String TAG = PhoneNumberFormattingTextWatcher.class.getSimpleName();

    private static final String SHIM_WARNING = "You are using a shim implementation of " +
            "PhoneNumberUtils. " +
            "This class is only available in the sample application and should _NOT_ be used in " +
            "production applications that are distributed to the Play Store";
    private static boolean HAVE_EMITTED_SHIM_WARNING = false;

    public static String formatNumberToE164(String number, String countryIso) {
        emitShimWarning();
        final String e164 = android.telephony.PhoneNumberUtils.formatNumberToE164(number, countryIso);
        return (null != e164) ? e164 : number;
    }

    public static boolean isPossibleNumber(String unused1, String unused2) {
        emitShimWarning();
        return true;
    }

    public static String getDefaultCountryIso(Context unused1) {
        emitShimWarning();
        return Locale.getDefault().getCountry();
    }

    private static void emitShimWarning() {
        // Only emit warning once
        if (!HAVE_EMITTED_SHIM_WARNING) {
            HAVE_EMITTED_SHIM_WARNING = true;
            Log.w(TAG, SHIM_WARNING);
        }
    }

}
