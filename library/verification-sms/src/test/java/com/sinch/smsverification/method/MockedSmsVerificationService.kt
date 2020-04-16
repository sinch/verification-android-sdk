package com.sinch.smsverification.method

import com.sinch.smsverification.SmsVerificationService
import com.sinch.smsverification.initialization.SmsInitiationResponseData
import com.sinch.smsverification.initialization.SmsVerificationInitiationData
import com.sinch.smsverification.verification.SmsVerificationData
import com.sinch.verificationcore.verification.response.VerificationResponseData
import retrofit2.Call
import retrofit2.mock.BehaviorDelegate
import retrofit2.mock.Calls

class MockedSmsVerificationService(private val delegate: BehaviorDelegate<SmsVerificationService>) :
    SmsVerificationService {

    var initializeVerificationCallCallback: (SmsVerificationInitiationData, String?) -> Call<SmsInitiationResponseData> =
        { _, _ -> Calls.failure(Exception()) }
    var verifyCallCallback: (String, SmsVerificationData) -> Call<VerificationResponseData> =
        { _, _ -> Calls.failure(Exception()) }

    override fun initializeVerification(
        data: SmsVerificationInitiationData,
        acceptedLanguages: String?
    ): Call<SmsInitiationResponseData> =
        delegate.returning(initializeVerificationCallCallback(data, acceptedLanguages))
            .initializeVerification(data, acceptedLanguages)

    override fun verifyNumber(
        number: String,
        data: SmsVerificationData
    ): Call<VerificationResponseData> =
        delegate.returning(verifyCallCallback(number, data)).verifyNumber(number, data)

}