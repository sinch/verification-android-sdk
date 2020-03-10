package com.sinch.verificationcore

import com.sinch.verificationcore.network.service.VerificationService
import retrofit2.Retrofit

class Test {

    private val api: VerificationService

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.reddit.com")
            .addConverterFactory(Gsonc))
            .build()

        redditApi = retrofit.create(RedditApi::class.java)
    }
}