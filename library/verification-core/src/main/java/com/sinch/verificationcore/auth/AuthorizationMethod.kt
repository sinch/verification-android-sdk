package com.sinch.verificationcore.auth

import okhttp3.Request

/**
 * General interface used for implementing different kind of ways to authorize Sinch API requests.
 */
interface AuthorizationMethod {
    /**
     * Method invoked before first request is made.
     */
    fun onPrepareAuthorization()

    /**
     * Method invoked each time API call is made. Implementation should modify and return authorized version of the request.
     * @param request Unauthorized request.
     * @return Authorized version of the request.
     */
    fun onAuthorize(request: Request): Request
}