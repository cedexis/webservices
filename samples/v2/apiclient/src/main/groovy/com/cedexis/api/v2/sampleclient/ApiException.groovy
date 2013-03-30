package com.cedexis.api.v2.sampleclient

/**
 * This exception is thrown when {@link CedexisApi} encounters an error.  It wraps
 * the error data in on or more {@link ApiErrorDescription}s that are available
 * for inspection.
 *
 * @author jon
 */
class ApiException extends RuntimeException {

    private final int httpStatus
    private final List<ApiErrorDescription> apiErrorDescriptions

    ApiException(int httpStatus, def json) {
        this.httpStatus = httpStatus
        this.apiErrorDescriptions = json.errorDetails.collect { new ApiErrorDescription(it) }
    }

    /** The HTTP status (404, etc) of the response. */
    int getHttpStatus() { httpStatus }

    /** List of specific errors. */
    List<ApiErrorDescription> getApiErrorDescriptions() { apiErrorDescriptions }

    @Override
    String toString() {
        "httpStatus: $httpStatus, errors: ${apiErrorDescriptions.size()}:\n$apiErrorDescriptions"
    }
}
