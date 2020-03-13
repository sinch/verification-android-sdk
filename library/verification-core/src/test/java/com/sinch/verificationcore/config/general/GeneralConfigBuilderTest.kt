package com.sinch.verificationcore.config.general

import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.sinch.verificationcore.auth.AppKeyAuthorizationMethod
import com.sinch.verificationcore.auth.AuthorizationMethod
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.junit.Assert.*


@RunWith(
    RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class GeneralConfigBuilderTest {

    @Test
    fun shouldBaseUrlMatch() {
        val baseUrl = "http://localhost.com/"
        val config = SinchGeneralConfig.Builder()
            .context(ApplicationProvider.getApplicationContext())
            .authMethod(mock())
            .apiHost(baseUrl).build()

         assertEquals(config.retrofit.baseUrl().toString(), baseUrl)
    }

}