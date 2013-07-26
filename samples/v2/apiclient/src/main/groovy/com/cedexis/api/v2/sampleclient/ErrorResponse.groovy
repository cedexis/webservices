package com.cedexis.api.v2.sampleclient

/**
 * Detailed information about a particular error.
 * See <a href="https://github.com/cedexis/webservices/wiki/V2-API-Metadata">the docs</a> for more.
 *
 * @author jon
 */
class ErrorResponse {
    private final int httpStatus
    private final List<ErrorDetail> errorDetails

    ErrorResponse(int httpStatus, def json) {
        this.httpStatus = httpStatus
        this.errorDetails = json.errorDetails.collect { new ErrorDetail(it) }
    }

    /** The HTTP status (404, etc) of the response. */
    int getHttpStatus() { httpStatus }

    /** List of specific errors. */
    List<ErrorDetail> getErrorDetails() { errorDetails }

    @Override
    String toString() {
        "httpStatus: $httpStatus, errors: ${errorDetails.size()}:\n$errorDetails"
    }

}
