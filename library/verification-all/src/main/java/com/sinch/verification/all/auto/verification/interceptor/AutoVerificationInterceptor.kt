package com.sinch.verification.all.auto.verification.interceptor

import android.content.Context
import com.sinch.verification.all.auto.initialization.AutoInitializationResponseData
import com.sinch.verification.core.verification.interceptor.BasicCodeInterceptor
import com.sinch.verification.core.verification.interceptor.CodeInterceptionListener
import com.sinch.verification.core.verification.interceptor.CodeInterceptor

/**
 * [CodeInterceptor] responsible for creating sub interceptors based on given [AutoInitializationResponseData].
 */
class AutoVerificationInterceptor(
    private val context: Context,
    private val autoInitializationResponseData: AutoInitializationResponseData,
    private val subCodeInterceptionListener: SubCodeInterceptionListener,
    private val subInterceptorFactory: SubCodeInterceptorFactory = SinchSubCodeInterceptorFactory(
        context = context,
        subCodeInterceptionListener = subCodeInterceptionListener
    ),
    autoCodeInterceptionListener: CodeInterceptionListener,
    interceptionTimeout: Long
) : BasicCodeInterceptor(interceptionTimeout, autoCodeInterceptionListener) {

    private var subInterceptors: List<CodeInterceptor> = emptyList()

    override fun onInterceptorStarted() {
        populateSubInterceptors()
        subInterceptors.forEach { it.start() }
    }

    override fun onInterceptorStopped() {}

    override fun onInterceptorTimedOut() {}

    private fun populateSubInterceptors() {
        subInterceptors = autoInitializationResponseData.let {
            listOf(it.smsDetails, it.flashcallDetails)
        }.mapNotNull { it?.let { subInterceptorFactory.create(it) } }
    }

}