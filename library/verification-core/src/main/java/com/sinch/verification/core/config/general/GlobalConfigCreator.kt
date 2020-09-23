package com.sinch.verification.core.config.general

import okhttp3.Interceptor

/**
 * Interface defining requirements of classes that can create [GlobalConfig] instances using fluent builder pattern.
 */
interface GlobalConfigCreator {

    /**
     * Assigns api host to the builder.
     * @param apiHost Custom api host of the backend ex: "https://example.com/"
     * @return Instance of creator with assigned apiHost.
     */
    fun apiHost(apiHost: String): GlobalConfigCreator

    /**
     * Assigns interceptors to the builder.
     * @param interceptors List of interceptors that are fired each time API request is made.
     * @return Instance of creator with assigned interceptors.
     */
    fun interceptors(interceptors: List<Interceptor>): GlobalConfigCreator

    /**
     * Build Global config instance.
     * @return Global config instance with previously defined parameters.
     */
    fun build(): GlobalConfig
}