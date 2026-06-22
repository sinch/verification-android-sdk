package com.sinch.verification.seamless.util

import com.sinch.verification.core.auth.AuthorizationInterceptor
import com.sinch.verification.seamless.BuildConfig
import com.sinch.verification.seamless.SeamlessHeaderInterceptor
import com.sinch.verification.seamless.config.SeamlessVerificationConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.net.SocketFactory

class SeamlessRetrofitProvider {
    companion object {

        const val INDIA_COUNTRY_CODE = "+91"

        /**
         * Builds a Retrofit instance for verification service with India-specific endpoint routing.
         *
         * This method creates a Retrofit instance configured with a specific socket factory for network binding.
         * For Indian phone numbers (starting with +91), it routes requests to the India-specific endpoint
         * defined in [BuildConfig.API_BASE_URL_IN]. For all other phone numbers, it uses the default
         * endpoint from the global configuration.
         *
         * The India-specific endpoint is used to handle seamless verification requests that require
         * special routing for Indian mobile networks.
         *
         * @param config Seamless verification configuration containing the phone number to verify
         * @param socketFactory Socket factory for binding requests to a specific network (e.g., cellular)
         * @return Configured Retrofit instance with appropriate base URL based on phone number country code
         */
        fun buildVerificationServiceRetrofitWithSocket(config: SeamlessVerificationConfig, socketFactory: SocketFactory): Retrofit {
            var baseUrl = config.globalConfig.retrofit.baseUrl().toString()
            val baseClient = config.globalConfig.retrofit.callFactory() as? OkHttpClient ?: OkHttpClient()

            val newClientBuilder = baseClient
                .newBuilder()
                .socketFactory(socketFactory)

            // Route to India-specific endpoint for Indian phone numbers (+91)
            if (config.number?.startsWith(INDIA_COUNTRY_CODE) == true) {
                // Ensure proper URL joining by removing trailing slash from base URL if present
                val baseUrlWithoutTrailingSlash = BuildConfig.API_BASE_URL_IN.removeSuffix("/")
                baseUrl = "$baseUrlWithoutTrailingSlash/verification/v1/"
            }

            return config.globalConfig.retrofit
                .newBuilder()
                .baseUrl(baseUrl)
                .client(newClientBuilder.build())
                .build()
        }

        /**
         * Builds a Retrofit instance for seamless verification requests with India-specific header interceptors.
         *
         * This method creates a new Retrofit instance from an existing one, removing the AuthorizationInterceptor
         * (as verification requests use the targetUri directly without authorization) and conditionally adding
         * debug headers for Indian phone numbers.
         *
         * For Indian phone numbers (starting with +91), it adds [SeamlessHeaderInterceptor] which includes
         * debug headers (x-msisdn and x-imsi) in debug builds. In release builds, the interceptor is a no-op.
         * For non-Indian numbers, no additional headers are added.
         *
         * @param retrofit Base Retrofit instance to build upon
         * @param number Phone number being verified (used to determine if India-specific headers are needed)
         * @return New Retrofit instance with appropriate interceptors configured
         */
        fun buildSeamlessVerificationRetrofit(retrofit : Retrofit, number: String?): Retrofit {
            val baseClient = retrofit.callFactory() as? OkHttpClient ?: OkHttpClient()

            // Create a new client without AuthorizationInterceptor
            // (verification requests use targetUri directly without authorization)
            val newClient = baseClient.newBuilder()
                .apply {
                    interceptors()
                        .filterNot { it is AuthorizationInterceptor }
                        .forEach { interceptor ->
                            addInterceptor(interceptor)
                        }
                    // Add additional headers required for India in debug mode
                    if (number?.startsWith(INDIA_COUNTRY_CODE) == true)
                        addInterceptor(SeamlessHeaderInterceptor())
                }
                .build()

            return retrofit
                .newBuilder()
                .client(newClient)
                .build()
        }
    }
}