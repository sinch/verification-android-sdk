package com.sinch.utils

val Long.Companion.MAX_TIMEOUT: Long get() = MAX_VALUE / 2 //for some reason if we use Long.MAX_VALUE on delayed Handler it simply runs instantly