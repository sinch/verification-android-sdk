package com.sinch.sinchverification

enum class Environment {

    PRODUCTION,
    APSE1,
    EUC1,
    FTEST1,
    FTEST2,
    CUSTOM;

    val predefinedURL: String
        get() {
            return when (this) {
                PRODUCTION -> BuildConfig.API_BASE_URL_PROD
                APSE1 -> BuildConfig.API_BASE_URL_PROD_APSE1
                EUC1 -> BuildConfig.API_BASE_URL_PROD_EUC1
                FTEST1 -> BuildConfig.API_BASE_URL_FTEST1
                FTEST2 -> BuildConfig.API_BASE_URL_FTEST2
                CUSTOM -> ""
            }
        }

    val predefinedAppKey: String
        get() {
            return when (this) {
                PRODUCTION -> BuildConfig.APP_KEY_PROD
                APSE1 -> BuildConfig.APP_KEY_PROD_APSE
                EUC1 -> BuildConfig.APP_KEY_PROD_EUC1
                FTEST1 -> BuildConfig.APP_KEY_FTEST1
                FTEST2 -> BuildConfig.APP_KEY_FTEST2
                CUSTOM -> ""
            }
        }

}