package com.sinch.verificationcore.config.general

import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.nhaarman.mockitokotlin2.mock
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config


@RunWith(
    RobolectricTestRunner::class
)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class GeneralConfigBuilderTest {

    @Test
    fun shouldBaseUrlMatch() {
        val apiHost = "http://localhost.com/"
        val config = SinchGeneralConfig.Builder()
            .context(ApplicationProvider.getApplicationContext())
            .authMethod(mock())
            .apiHost(apiHost).build()
        
        assertTrue(config.retrofit.baseUrl().toString().startsWith(apiHost))
    }

}