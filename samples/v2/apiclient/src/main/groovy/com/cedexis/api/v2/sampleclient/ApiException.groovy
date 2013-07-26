package com.cedexis.api.v2.sampleclient

/**
 * This exception is thrown when {@link CedexisApi} encounters an error.  It wraps
 * the error data in on or more {@link ErrorDetail}s that are available
 * for inspection.
 *
 * @author jon
 */
class ApiException extends RuntimeException {

    private final ErrorResponse errorResponse

    ApiException(ErrorResponse errorResponse) {
        this.errorResponse = errorResponse
    }

    /** The detailed error response object containing the http status and error descriptions. */
    ErrorResponse getErrorResponse() { errorResponse }

    @Override
    String toString() {
        "errorResponse: $errorResponse"
    }
}
