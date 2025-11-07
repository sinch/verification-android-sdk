package com.sinch.verification.core.config.method

import com.sinch.verification.core.internal.VerificationState
import com.sinch.verification.core.verification.interceptor.CodeInterceptor
import com.sinch.verification.core.verification.interceptor.InterceptorState
import com.sinch.verification.core.verification.response.EmptyVerificationListener
import com.sinch.verification.core.verification.response.VerificationListener

/**
 * Class containing common logic for verification method that can automatically intercept verification codes.
 * @param verificationServiceConfig Verification method specific configuration reference.
 * @param Service Retrofit service class used for communication with the backend.
 * @param verificationListener Verification listener to be notified about verification process.
 */
abstract class AutoInterceptedVerificationMethod<Service, Interceptor : CodeInterceptor>(
    verificationServiceConfig: VerificationMethodConfig<Service>,
    verificationListener: VerificationListener = EmptyVerificationListener()
) : VerificationMethod<Service>(verificationServiceConfig, verificationListener) {

    protected abstract var codeInterceptor: Interceptor?

    override fun stop() {
        super.stop()
        codeInterceptor?.stop()
    }

    override fun update(newState: VerificationState) {
        super.update(newState)
        if (newState.isVerificationProcessFinished && codeInterceptor?.state != InterceptorState.DONE) {
            codeInterceptor?.stop()
        }
    }
}