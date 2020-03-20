package com.sinch.metadata.collector

import android.content.Context
import com.sinch.utils.api.ApiLevelUtils

class BasicLocaleCollector(private val context: Context) : LocaleCollector {

    override fun collect(): String = if (ApiLevelUtils.isApi24OrLater) {
        context.resources.configuration.locales[0]
    } else {
        @Suppress("DEPRECATION")
        context.resources.configuration.locale
    }.toString()

}