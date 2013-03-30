package com.cedexis.api.v2.sampleclient

/**
 * Detailed information about a particular error.
 * See <a href="https://github.com/cedexis/webservices/wiki/V2-API-Metadata">the docs</a> for more.
 *
 * @author jon
 */
class ApiErrorDescription {
    private final String developerMessage
    private final String userMessage
    private final String field
    private final String errorCode
    private final String moreInfo

    ApiErrorDescription(apiErrorDescription) {
        this.developerMessage = apiErrorDescription.developerMessage
        this.userMessage = apiErrorDescription.userMessage
        this.field = apiErrorDescription.field
        this.errorCode = apiErrorDescription.errorCode
        this.moreInfo = apiErrorDescription.moreInfo
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
