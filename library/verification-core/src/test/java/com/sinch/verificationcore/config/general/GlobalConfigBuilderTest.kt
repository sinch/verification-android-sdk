package com.sinch.verificationcore.config.general

import android.os.Build
import androidx.test.core.app.ApplicationProvider
import io.mockk.mockk
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config


@RunWith(
    RobolectricTestRunner::class
)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class GlobalConfigBuilderTest {

    @Test
    fun shouldBaseUrlMatch() {
        val apiHost = "http://localhost.com/"
        val config =
            SinchGlobalConfig.Builder.instance.applicationContext(ApplicationProvider.getApplicationContext())
                .authorizationMethod(mockk())
                .apiHost(apiHost)
                .build()

        assertTrue(config.retrofit.baseUrl().toString().startsWith(apiHost))
    }

}