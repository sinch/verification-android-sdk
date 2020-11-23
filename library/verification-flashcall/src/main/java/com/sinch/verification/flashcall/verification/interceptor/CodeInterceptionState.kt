package com.sinch.verification.flashcall.verification.interceptor

import com.sinch.verification.core.verification.interceptor.CodeInterceptor

/**
 * Enum defining possible states of the [CodeInterceptor].
 */
enum class CodeInterceptionState {

    /**
     * The interceptor has not intercepted any code yet.
     */
    NONE,

    /**
     * The interceptor has intercepted a code, however it happened after [CodeInterceptor.interceptionTimeout].
     */
    LATE,

    /**
     * The interceptor has successfully intercepted the code (before [CodeInterceptor.interceptionTimeout]).
     */
    NORMAL;
}