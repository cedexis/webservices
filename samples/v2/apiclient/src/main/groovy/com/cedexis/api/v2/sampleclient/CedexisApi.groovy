package com.cedexis.api.v2.sampleclient
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC

import groovyx.net.http.RESTClient
import org.apache.http.conn.scheme.Scheme
import org.apache.http.conn.ssl.SSLSocketFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import java.security.SecureRandom
import java.security.cert.X509Certificate
/**
 * Simple Groovy interface for using the Cedexis API
 * @author jon
 */
class CedexisApi {

    // -------------------------------------------------------------------------
    // set up
    // -------------------------------------------------------------------------
    private static final Logger LOG = LoggerFactory.getLogger(CedexisApi)

    private final uri
    private final clientId
    private final clientSecret
    private final accessToken
    private final RESTClient restClient
    private acceptLanguage

    CedexisApi(String uri, String clientId, String clientSecret) {
        LOG.info "uri: $uri, client_id: $clientId, client_secret: ${clientSecret[0..5]}..."
        this.uri = require uri, 'uri'
        this.restClient = trustingRESTClient()
        this.clientId = require clientId, 'client_id'
        this.clientSecret = require clientSecret, 'client_secret'
        this.accessToken = require requestAccessToken(), 'access_token'
        this.acceptLanguage = null
    }

    // -------------------------------------------------------------------------
    // API methods
    // -------------------------------------------------------------------------
    /** Requests an access_token.  Called automatically upon construction of CedexisApi, but can be called again
     * if the access_token expires. */
    String requestAccessToken() {
        def postBody = [
                client_id: this.clientId,
                client_secret: this.clientSecret,
                grant_type: 'client_credentials'
        ]

        restClient.post(path: p('/oauth/token'), body: postBody, requestContentType: URLENC) { resp, json ->
            json.value
        }
    }

    /** Sets the language passed as the HTTP Accept-Language header when making
     * requests.  Use null for the default language. */
    void setAcceptLanguage(String acceptLanguage) {
        this.acceptLanguage = acceptLanguage
    }

    /** Does a simple HTTP GET with the appropriate api base path (/api)
     * and headers - returns the JSON response. */
    def get(path, query = [:]) throws ApiException {
        restClient.get(path: p(path), query: query, contentType: JSON,
                headers: headers()) { resp, json ->
            [response: resp, 'json': json]
        }
    }

    /** Does a simple HTTP POST with the appropriate api base path (/api)
     * and headers - returns the JSON response. */
    def post(path, body = [:]) throws ApiException {
        restClient.post(path: p(path), body: body, contentType: JSON,
                requestContentType: JSON, headers: headers()) { resp, json ->
            [response: resp, 'json': json]
        }
    }

    /** Does a simple HTTP PUT with the appropriate api base path (/api)
     * and headers - returns the JSON response. */
    def put(path, body = [:]) throws ApiException {
        restClient.put(path: p(path), body: body, contentType: JSON,
                requestContentType: JSON, headers: headers()) { resp, json ->
            [response: resp, 'json': json]
        }
    }

    /** Does a simple HTTP DELETE with the appropriate api base path (/api)
     * and headers - returns the JSON response. */
    def delete(path, body = [:]) throws ApiException {
        restClient.delete(path: p(path), body: body, contentType: JSON,
                requestContentType: JSON, headers: headers()) { resp, json ->
            [response: resp, 'json': json]
        }
    }

    // -------------------------------------------------------------------------
    // utils
    // -------------------------------------------------------------------------
    /** Sets the Authorization: and Accept-Language: HTTP headers. */
    private headers() {
        [
                Authorization: "Bearer $accessToken",
                'Accept-Language': this.acceptLanguage
        ]
    }

    private String p(String path) {
        '/api' + (path.startsWith('/') ? '' : '/') + path
    }

    /** Configures a RESTClient to trust self signed SSL certs if the uri
     * contains "localhost" or "dev". */
    private RESTClient trustingRESTClient() {
        def http = new RESTClient(uri)
        if (uri.contains('localhost') || uri.contains('dev')) {
            def sslContext = SSLContext.getInstance('SSL')
            sslContext.init(null, [new X509TrustManager() {
                @Override
                void checkClientTrusted(X509Certificate[] certs, String authType) { }

                @Override
                void checkServerTrusted(X509Certificate[] certs, String authType) { }

                @Override
                X509Certificate[] getAcceptedIssuers() { null }
            } ] as TrustManager[], new SecureRandom())
            def sf = new SSLSocketFactory(sslContext, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER)
            def httpsScheme = new Scheme('https', sf, 443)
            http.client.connectionManager.schemeRegistry.register(httpsScheme)
        }

        // automatically throw a descriptive error if the call wasn't successful
        http.handler.failure = { resp, json ->
            throw new ApiException(new ErrorResponse(resp.status, json))
        }
        http
    }

    private static require(target, name) {
        if (!target) { throw new IllegalArgumentException("$name is required") }
        target
    }
}
