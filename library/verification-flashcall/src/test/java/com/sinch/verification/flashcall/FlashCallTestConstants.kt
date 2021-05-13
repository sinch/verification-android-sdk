package com.sinch.verification.flashcall

internal object ApiTimeouts {
    const val interceptionTimeout = 60L
    const val reportTimeout = 75L
}

internal object CliTemplates {
    const val template1 = "(.*)123(.*)"
    const val correctNumber1 = "+48000123000"
}

internal object CallHistory {
    const val historyNum1 = "+48123456"
    const val historyNum2 = "+481236789"
    const val historyNum3 = "+22000000"
}

internal object Constants {
    const val testID = "testID"
    const val phone = "+48123456789"
    const val phoneMatchingTemplate1 = "01234"
    const val phoneNonMatchingTemplate1 = "024"
}