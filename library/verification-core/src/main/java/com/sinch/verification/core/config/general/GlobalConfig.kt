package com.sinch.verification.core.config.general

import android.content.Context
import javax.net.SocketFactory
import retrofit2.Retrofit

/**
 * Interface defining requirements of global SDK configuration.
 */
interface GlobalConfig {

    /**
     * Application context reference. DO NOT use activity as context here as it may outlive global config instances.
     */
    val context: Context

    /**
     * Retrofit instance used for communication with Sinch API.
     */
    val retrofit: Retrofit

    /**
     * Factory method for creating Retrofit using specific [SocketFactory].
     * This can be used to eg. force specific network (cellular in case of seamless verification) for
     * given request. Note that most of the configuration properties (such as provided interceptors or
     * timeouts) should be same as for globally used Retrofit.
     *
     * @return Retrofit instance using specified socketFactory.
     */
    fun socketFactoryRetrofit(socketFactory: SocketFactory): Retrofit
}