package com.sinch.verificationcore.config

import android.content.Context
import com.sinch.verificationcore.auth.AuthorizationMethod
import com.sinch.verificationcore.networking.AuthorizationInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SinchGlobalGeneralConfig private constructor(
    override val context: Context,
    override val retrofit: Retrofit
) : GeneralConfig {

    class Builder : ConfigBuilder {

        private lateinit var context: Context
        private lateinit var apiHost: String
        private lateinit var authorizationMethod: AuthorizationMethod

        private var interceptors: List<Interceptor> = emptyList()

        override fun build(): GeneralConfig {
            val okHttpClient =
                OkHttpClient().newBuilder()
                    .addInterceptor(AuthorizationInterceptor(authorizationMethod))
                    .apply { interceptors().forEach { addInterceptor(it) } }
                    .build()
            val retrofit = Retrofit.Builder()
                .baseUrl(apiHost)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()
            return SinchGlobalGeneralConfig(context, retrofit)
        }

        override fun context(context: Context): ConfigBuilder = apply { this.context = context }

        override fun authMethod(authorizationMethod: AuthorizationMethod): ConfigBuilder =
            apply { this.authorizationMethod = authorizationMethod }

        override fun apiHost(apiHost: String): ConfigBuilder = apply { this.apiHost = apiHost }

        override fun interceptors(interceptors: List<Interceptor>): ConfigBuilder =
            apply { this.interceptors = interceptors }

    }

}