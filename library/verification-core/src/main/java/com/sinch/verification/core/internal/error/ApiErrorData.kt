package com.sinch.verification.core.internal.error

import com.sinch.verification.core.internal.error.ApiErrorData.ErrorCodes.BadRequest.NumberMissingLeadingPlus
import com.sinch.verification.core.internal.error.ApiErrorData.ErrorCodes.BadRequest.ParameterValidation
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Class containing detailed information about what went wrong during the API call. (Server did not return 2xx status).
 * @property errorCode Integer defining specific error. See [ErrorCodes] for possible values with explanation.
 * @property message Human readable message describing why API call has failed
 * @property reference Optional reference id that was passed with the request.
 */
@Serializable
data class ApiErrorData(
    @SerialName("errorCode") val errorCode: Int? = null,
    @SerialName("message") val message: String? = null,
    @SerialName("reference") val reference: String? = null
) {

    /**
     * Object defining possible [ApiErrorData.errorCode] values with explanation.
     */
    object ErrorCodes {

        object BadRequest {
            const val ParameterValidation = 40001
            const val NumberMissingLeadingPlus = 40005
            const val InvalidRequest = 40003;
            const val InvalidAuthorizationKey = 40004;
        }

        object Unauthorized {
            const val AuthorizationHeader = 40100;
            const val TimestampHeader = 40101;
            const val InvalidSignature = 40102;
            const val AlreadyAuthorized = 40103;
            const val AuthorizationRequired = 40104;
            const val Expired = 40105;
            const val UserBarred = 40106;
            const val InvalidAuthorization = 40107;
            const val InvalidCredentials = 40108;
        }

        object PaymentRequired {
            const val NotEnoughCredit = 40200;
        }

        object Forbidden {
            const val ForbiddenRequest = 40300;
            const val InvalidScheme = 40301;
            const val InsufficientPrivileges = 40302;
            const val RestrictedAction = 40303;
        }

        object NotFound {
            const val ResourceNotFound = 40400;
        }

        object Conflict {
            const val RequestConflict = 40900;
        }

        object UnprocessableEntity {
            const val ApplicationConfiguration = 42200;
            const val Unavailable = 42201;
            const val InvalidCallbackResponse = 42202;
        }

        object TooManyRequests {
            const val CapacityExceeded = 42900;
            const val VelocityConstraint = 42901;
        }

        object InternalServerError {
            const val InternalError = 50000;
        }

        object NotImplemented {
            const val MethodNotImplemented = 50100;
            const val StatusNotImplemented = 50101;
        }

        object ServiceUnavailable {
            const val TemporaryDown = 50300;
            const val ConfigurationError = 50301;
        }

    }

    /**
     * Flag indicating if error was probably caused my malformed phone number passed to request
     * (too short, too long, wrong characters etc.). Note that this flag can return true even when the cause was actually
     * different as the error code can be only be checked against 'ParameterValidation' error constant.
     */
    val mightBePhoneFormattingError: Boolean
        get() =
            errorCode == ParameterValidation || errorCode == NumberMissingLeadingPlus

}