package com.sinch.smsverification.method

import com.sinch.verification.core.verification.model.VerificationData
import com.sinch.verification.core.verification.response.VerificationResponseData
import com.sinch.verification.sms.SmsVerificationService
import com.sinch.verification.sms.initialization.SmsInitiationResponseData
import com.sinch.verification.sms.initialization.SmsVerificationInitiationData
import retrofit2.Call
import retrofit2.mock.BehaviorDelegate
import retrofit2.mock.Calls

class MockedSmsVerificationService(private val delegate: BehaviorDelegate<SmsVerificationService>) :
    SmsVerificationService {

    var initializeVerificationCallCallback: (SmsVerificationInitiationData, String?) -> Call<SmsInitiationResponseData> =
        { _, _ -> Calls.failure(Exception()) }
    var verifyCallCallback: (String, VerificationData) -> Call<VerificationResponseData> =
        { _, _ -> Calls.failure(Exception()) }

    override fun initializeVerification(
        data: SmsVerificationInitiationData,
        acceptedLanguages: String?
    ): Call<SmsInitiationResponseData> =
        delegate.returning(initializeVerificationCallCallback(data, acceptedLanguages))
            .initializeVerification(data, acceptedLanguages)

    override fun verifyNumber(
        number: String,
        data: VerificationData
    ): Call<VerificationResponseData> =
        delegate.returning(verifyCallCallback(number, data)).verifyNumber(number, data)

    override fun verifyById(
        subVerificationId: String,
        data: VerificationData
    ): Call<VerificationResponseData> {
        TODO("Not used in tests")
    }

    override fun verifySeamless(url: String): Call<VerificationResponseData> {
        TODO("Not used in tests")
    }

}