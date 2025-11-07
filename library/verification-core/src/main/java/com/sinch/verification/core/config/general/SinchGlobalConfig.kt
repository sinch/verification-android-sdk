package com.sinch.verification.core.config.general

import android.content.Context
import com.sinch.verification.core.auth.AuthorizationInterceptor
import com.sinch.verification.core.auth.AuthorizationMethod
import com.sinch.verificationcore.BuildConfig
import java.util.concurrent.TimeUnit
import javax.net.SocketFactory
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

/**
 * Sinch specific global SDK configuration.
 * @param context Application context reference
 * @param retrofit Retrofit reference
 * @see SinchGlobalConfig.Builder
 */
class SinchGlobalConfig private constructor(
    override val context: Context,
    override val retrofit: Retrofit,
    private val httpClientInterceptors: List<Interceptor>
) : GlobalConfig {

    companion object {
        const val OKHTTP_READ_TIMEOUT = 30L // in seconds
        const val OKHTTP_CONNECT_TIMEOUT = 30L // in seconds
    }

    override fun socketFactoryRetrofit(socketFactory: SocketFactory): Retrofit =
        retrofit.newBuilder().client(
            OkHttpClient().newBuilder()
                .readTimeout(OKHTTP_READ_TIMEOUT, TimeUnit.SECONDS)
                .connectTimeout(OKHTTP_CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .socketFactory(socketFactory)
                .apply { httpClientInterceptors.forEach { addInterceptor(it) } }
                .build()
        ).build()

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
                    .readTimeout(OKHTTP_READ_TIMEOUT, TimeUnit.SECONDS)
                    .connectTimeout(OKHTTP_CONNECT_TIMEOUT, TimeUnit.SECONDS)
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
                    Json {
                        encodeDefaults = true
                        ignoreUnknownKeys = true
                    }.asConverterFactory("application/json".toMediaType())
                )
                .client(okHttpClient)
                .build()
            return SinchGlobalConfig(
                context,
                retrofit,
                additionalInterceptors
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