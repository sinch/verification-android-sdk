package com.sinch.verification.flashcall

import android.app.Application
import android.os.Build
import android.provider.CallLog
import androidx.test.core.app.ApplicationProvider
import com.sinch.verification.flashcall.CallHistory.historyNum1
import com.sinch.verification.flashcall.CallHistory.historyNum2
import com.sinch.verification.flashcall.CallHistory.historyNum3
import com.sinch.verification.flashcall.verification.callhistory.ContentProviderCallHistoryReader
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.fakes.RoboCursor
import org.robolectric.shadows.ShadowContentResolver
import java.util.*


@RunWith(
    RobolectricTestRunner::class
)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class ContentProviderCallHistoryTests {

    private val context = ApplicationProvider.getApplicationContext<Application>()
    private val contentResolverShadow =
        Shadows.shadowOf(context.contentResolver)

    companion object {
        fun insertMockedHistory(shadowResolver: ShadowContentResolver, initialTimeStamp: Long) {
            RoboCursor().apply {
                setResults(
                    arrayOf(
                        arrayOf(
                            initialTimeStamp - 2,
                            historyNum1,
                            CallLog.Calls.OUTGOING_TYPE
                        ),
                        arrayOf(
                            initialTimeStamp - 20,
                            historyNum2,
                            CallLog.Calls.MISSED_TYPE
                        ),
                        arrayOf(
                            initialTimeStamp - 200,
                            historyNum3,
                            CallLog.Calls.INCOMING_TYPE
                        )
                    )
                )
                setColumnNames(listOf(CallLog.Calls.DATE, CallLog.Calls.NUMBER, CallLog.Calls.TYPE))
            }.let { shadowResolver.setCursor(it) }
        }
    }

    @Test
    fun testReaderFindsCorrectNumbers() {
        val initialDate = Date()
        insertMockedHistory(contentResolverShadow, initialDate.time)
        val foundCalls =
            ContentProviderCallHistoryReader(context.contentResolver).readIncomingCalls(initialDate.time - 1000)
        assertTrue(listOf(historyNum2, historyNum3) == foundCalls)
    }

}