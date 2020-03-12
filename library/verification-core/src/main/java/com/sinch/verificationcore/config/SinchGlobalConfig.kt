package com.sinch.verificationcore.config

import android.content.Context
import com.sinch.verificationcore.auth.AuthMethod
import com.sinch.verificationcore.networking.AuthInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SinchGlobalConfig private constructor(
    override val context: Context,
    override val retrofit: Retrofit
) : Config {

    class Builder : ConfigBuilder {

        private lateinit var context: Context
        private lateinit var apiHost: String
        private lateinit var authMethod: AuthMethod

        override fun build(): Config {
            val okHttpClient =
                OkHttpClient().newBuilder()
                    .addInterceptor(AuthInterceptor(authMethod))
                    .build()
            val retrofit = Retrofit.Builder()
                .baseUrl(apiHost)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()
            return SinchGlobalConfig(context, retrofit)
        }

        override fun context(context: Context): ConfigBuilder = apply { this.context = context }
        override fun authMethod(authMethod: AuthMethod): ConfigBuilder =
            apply { this.authMethod = authMethod }

        override fun apiHost(apiHost: String): ConfigBuilder = apply { this.apiHost = apiHost }

    }


}