package com.sinch.verification.core.verification

import android.os.Parcelable
import com.sinch.verification.utils.prefixed
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlinx.android.parcel.Parcelize

@Parcelize
data class VerificationLanguage(
    val language: String,
    val region: String? = null,
    val weight: Double? = null
) : Parcelable {

    companion object {
        const val REGION_PREFIX = "-"
        const val WEIGHT_PREFIX = ";q="
        const val DECIMAL_PATTERN = "#.###"
    }

    init {
        if (weight != null && (weight > 1 || weight < 0)) {
            throw IllegalArgumentException("The weight value should be within range 0<=weight<=1")
        }
    }

    val httpHeader: String get() = asHttpHeader()

    private val weightString: String?
        get() = DecimalFormat(DECIMAL_PATTERN, DecimalFormatSymbols(Locale.UK)).run {
            weight?.let { format(it) }
        }

    private fun asHttpHeader(): String {
        val prefixedRegion = region?.prefixed(REGION_PREFIX)
        val prefixedWeight = weightString?.prefixed(WEIGHT_PREFIX)
        return "$language${prefixedRegion.orEmpty()}${prefixedWeight.orEmpty()}"
    }
}

fun List<VerificationLanguage>.asLanguagesString() =
    if (isEmpty())
        null
    else
        fold("") { accumulator, language ->
            "$accumulator,${language.httpHeader}"
        }.removePrefix(",")