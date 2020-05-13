package com.sinch.verificationcore.config.general

import android.content.Context
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.sinch.verificationcore.BuildConfig
import com.sinch.verificationcore.auth.AuthorizationInterceptor
import com.sinch.verificationcore.auth.AuthorizationMethod
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit

/**
 * Sinch specific global SDK configuration.
 * @param context Application context reference
 * @param retrofit Retrofit reference
 * @see SinchGlobalConfig.Builder
 */
class SinchGlobalConfig private constructor(
    override val context: Context,
    override val retrofit: Retrofit
) : GlobalConfig {

    /**
     * Builder implementing [fluent builder](https://dzone.com/articles/fluent-builder-pattern) pattern to create global config objects.
     */
    class Builder private constructor() : ApplicationContextSetter, AuthorizationMethodSetter,
        GlobalConfigCreator {

        companion object {
            /**
             * Instance of builder that should be used to create global config object.
             */
            @JvmStatic
            val instance: ApplicationContextSetter
                get() = Builder()
        }

        private lateinit var context: Context
        private lateinit var authorizationMethod: AuthorizationMethod

        private var apiHost: String = BuildConfig.API_BASE_URL
        private var additionalInterceptors: List<Interceptor> = emptyList()

        private val baseUrl: String get() = "${apiHost}verification/v1/"

        override fun build(): GlobalConfig {
            val okHttpClient =
                OkHttpClient().newBuilder()
                    .addInterceptor(
                        AuthorizationInterceptor(
                            authorizationMethod
                        )
                    )
                    .apply { additionalInterceptors.forEach { addInterceptor(it) } }
                    .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(
                    Json(JsonConfiguration.Stable.copy(ignoreUnknownKeys = true))
                        .asConverterFactory("application/json".toMediaType())
                )
                .client(okHttpClient)
                .build()
            return SinchGlobalConfig(
                context,
                retrofit
            )
        }

        override fun applicationContext(applicationContext: Context): AuthorizationMethodSetter =
            apply { this.context = applicationContext }

        override fun authorizationMethod(authorizationMethod: AuthorizationMethod): GlobalConfigCreator =
            apply { this.authorizationMethod = authorizationMethod }

        override fun apiHost(apiHost: String): GlobalConfigCreator =
            apply { this.apiHost = apiHost }

        override fun interceptors(interceptors: List<Interceptor>): GlobalConfigCreator =
            apply { this.additionalInterceptors = interceptors }

    }

}