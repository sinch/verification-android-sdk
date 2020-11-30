package com.sinch.verification.all.auto.verification.interceptor

import com.sinch.verification.core.initiation.response.InitiationDetails
import com.sinch.verification.core.verification.interceptor.CodeInterceptor

/**
 * Creates [CodeInterceptor] based on [InitiationDetails] returned by Sinch Rest API.
 */
interface SubCodeInterceptorFactory {

    /**
     * Creates [CodeInterceptor] based on [InitiationDetails] returned by Sinch Rest API.
     * @param details Initiation details specific for sub verification method (for auto verification).
     * @return [CodeInterceptor] that can intercept verification code for given verification details or null if automatic interception is not possible.
     */
    fun create(details: InitiationDetails): CodeInterceptor?
}