package com.sinch.verification.utils

import android.content.Context
import android.telephony.PhoneNumberUtils
import android.telephony.TelephonyManager
import java.util.Locale

/**
 * Helper object holding methods connected with phone number validation, formatting and processing.
 */
object SinchPhoneNumberUtils {

    /**
     * Checks if provided phone number is valid for given country code.
     * @param phoneNumber Phone number to be checked.
     * @param countryIso The ISO 3166-1 two letters country code.
     * @return True if phone number is valid, false otherwise.
     */
    fun isPossiblePhoneNumber(phoneNumber: String, countryIso: String) =
        PhoneNumberUtils.formatNumberToE164(phoneNumber, countryIso) != null

    /**
     * Returns country ISO 3166-1 code based on installed sim card if present or device's locale.
     * @param context Android context instance.
     * @return The ISO 3166-1 two letters country code.
     */
    fun getDefaultLocale(context: Context): String {
        val telephonyManager =
            context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return if (telephonyManager.simState == TelephonyManager.SIM_STATE_READY) {
            telephonyManager.simCountryIso.toUpperCase(Locale.ROOT)
        } else {
            Locale.getDefault().country
        }
    }

}