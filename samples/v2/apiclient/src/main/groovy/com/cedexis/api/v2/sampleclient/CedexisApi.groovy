package com.cedexis.api.v2.sampleclient

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC

import groovyx.net.http.HTTPBuilder
import org.apache.http.conn.scheme.Scheme
import org.apache.http.conn.ssl.SSLSocketFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.text.SimpleDateFormat

/**
 *
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
    private final http
    private acceptLanguage

    CedexisApi(String uri, String clientId, String clientSecret) {
        LOG.info "uri: $uri, client_id: $clientId, client_secret: ${clientSecret[0..5]}..."
        this.uri = require uri, 'uri'
        this.http = trustingHTTPBuilder()
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

        http.post(path: '/api/oauth/token', body: postBody, requestContentType: URLENC) { resp, json ->
            json.value
        }
    }

    /** Sets the language passed as the HTTP Accept-Language header when making
     * requests.  Use null for the default language. */
    void setAcceptLanguage(String acceptLanguage) {
        this.acceptLanguage = acceptLanguage
    }

    /** Pings the server and returns the current server time. */
    Date ping() {
        def json = get 'system.json/ping'
        def datetime = json.datetime
        new SimpleDateFormat('yyyy-MM-dd hh:mm:ssz', Locale.US).parse(datetime)
    }

    String apiVersion() {
        def json = get 'system.json/version'
        json.version
    }

    def listApplications() { get 'applications.json' }
    def listContinents() { get 'continents.json' }
    def listSubcontinents() { get 'subcontinents.json' }
    def listCountries() { get 'countries.json' }
    def listCustomers() { get 'customers.json' }
    def listNetworks(q) { get 'networks.json', [q: q] }
    def listProbeTypes() { get 'probetypes.json' }
    def listUserAgents(q) { get 'useragents.json', [q: q] }
    def listStatistics() { get 'statistics.json' }
    def listProviders(visibility) { get 'providers.json', [visibility: visibility] }
    def listReferrers() { get 'referrers.json' }
    def addReferrer(url) { post 'referrers.json', [url: url] }
    def loadReferrer(id) { get 'referrers.json/' + id }
    def queryRadarFacts(Map params) { get 'radarfacts.json', params }

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

    /** Does a simple HTTP GET with the appropriate api path (/api/v2/)
     * and headers - returns the JSON response. */
    def get(path, query = [:]) {
        http.get(path: "/api/v2/${path}", query: query, contentType: JSON,
                headers: headers()) { resp, json ->
            json
        }
    }

    /** Does a simple HTTP POST with the appropriate api path (/api/v2/)
     * and headers - returns the JSON response. */
    def post(path, body = [:]) {
        http.post(path: "/api/v2/${path}", body: body, contentType: JSON,
                requestContentType: JSON, headers: headers()) { resp, json ->
            json
        }
    }

    /** Configures an HTTPBuilder to trust self signed SSL certs if the uri
     * contains "localhost" or "dev". */
    private HTTPBuilder trustingHTTPBuilder() {
        def http = new HTTPBuilder(uri)
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
            throw new ApiException(resp.status, json)
        }
        http
    }

    private static require(target, name) {
        if (!target) { throw new IllegalArgumentException("$name is required") }
        target
    }


}
