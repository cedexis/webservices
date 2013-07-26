package com.cedexis.api.v2.sampleclient

/**
 * Detailed information about a particular error.
 * See <a href="https://github.com/cedexis/webservices/wiki/V2-API-Metadata">the docs</a> for more.
 *
 * @author jon
 */
class ErrorDetail {
    private final String developerMessage
    private final String userMessage
    private final String field
    private final String errorCode
    private final String moreInfo

    ErrorDetail(errorDetail) {
        this.developerMessage = errorDetail.developerMessage
        this.userMessage = errorDetail.userMessage
        this.field = errorDetail.field
        this.errorCode = errorDetail.errorCode
        this.moreInfo = errorDetail.moreInfo
    }

    /** A plain English message that a developer can use to debug the problem. */
    String getDeveloperMessage() { developerMessage }

    /** A message that can be shown to the end user (optional). */
    String getUserMessage() { userMessage }

    /** The field that the error relates to (optional). */
     String getField() { field }

    /** An internal system error code that you should include if talking with Cedexis support about the error. */
    String getErrorCode() { errorCode }

    /** A link to a page with more information regarding the error, or any other helpful information (optional). */
    String getMoreInfo() { moreInfo }

    @Override
    String toString() {
        """[
    developerMessage: $developerMessage
    userMessage: $userMessage
    field: $field
    errorCode: $errorCode
    moreInfo: $moreInfo
]"""
    }

}
