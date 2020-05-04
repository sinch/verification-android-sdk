package com.sinch.verificationcore.verification

import com.sinch.logging.logger
import com.sinch.verificationcore.internal.utils.ApiCallback
import retrofit2.Response

/**
 * Convenient [ApiCallback] that simply logs the result.
 */
class IgnoredApiCallback<Data> : ApiCallback<Data> {

    private val logger = logger()

    override fun onSuccess(data: Data, response: Response<Data>) {
        logger.debug("onSuccess called with data: $data")
    }

    override fun onError(t: Throwable) {
        logger.error("onError called with throwable: $t")
    }

}

typealias IgnoredUnitApiCallback = IgnoredApiCallback<Unit>