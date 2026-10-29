package com.sinch.verification.seamless.config

import android.app.Activity
import com.sinch.verification.core.config.VerificationMethodConfigCreator

interface SeamlessVerificationConfigCreator :
    VerificationMethodConfigCreator<SeamlessVerificationConfigCreator, SeamlessVerificationConfig> {

    /**
     * Provides an [Activity] context required by the v2 digital credential flow. The credential
     * picker UI cannot attach to an application context, so this must be set when v2 responses
     * are expected. Stored as a weak reference to avoid leaks.
     */
    fun activity(activity: Activity): SeamlessVerificationConfigCreator
}