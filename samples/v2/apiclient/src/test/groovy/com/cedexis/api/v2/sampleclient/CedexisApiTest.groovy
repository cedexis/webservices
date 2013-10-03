package com.cedexis.api.v2.sampleclient

import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * The uri, client_id, or client_secret must be set with command line args:
 * <ul>
 * <li><tt>-DCedexisApiTest.uri=https://...</tt></li>
 * <li><tt>-DCedexisApiTest.client_id=...</tt></li>
 * <li><tt>-DCedexisApiTest.client_secret=...</tt></li>
 * </ul>
 *
 * @author jon
 */
class CedexisApiTest {

    private static final Logger LOG = LoggerFactory.getLogger(CedexisApiTest)
    public static final String URI_SYSTEM_PROPERTY = CedexisApiTest.simpleName + '.uri'
    public static final String CLIENT_ID_SYSTEM_PROPERTY = CedexisApiTest.simpleName + '.client_id'
    public static final String CLIENT_SECRET_SYSTEM_PROPERTY = CedexisApiTest.simpleName + '.client_secret'

    private CedexisApi api

    @Before
    void setUp() {
        api = new CedexisApi(
                requireSystemProp(URI_SYSTEM_PROPERTY),
                requireSystemProp(CLIENT_ID_SYSTEM_PROPERTY),
                requireSystemProp(CLIENT_SECRET_SYSTEM_PROPERTY))
    }

    private static requireSystemProp(key) {
        def value = System.getProperty key
        if (!value) {
            throw new IllegalArgumentException("$key is required (try '-D$key=...' on the command line")
        }
        value
    }

    private void printAll(results) {
        LOG.debug results.size + ' results'
        results.each { LOG.debug "$it" }
    }

    @Test
    void testPing() {
        def got = api.get '/v2/meta/system.json/ping'
        assert 200 == got.response.status
        LOG.debug "ping response: $got.json.result"
        assert 'pong' == got.json.result
    }

    @Test
    void testApiVersion() {
        def got = api.get '/v2/meta/system.json/version'
        def apiVersion = got.json.result
        LOG.debug "apiVersion: $apiVersion"
        assert 'v2' == apiVersion
    }

    @Test
    void testListDnsApplications() {
        def applications = api.get('/v2/reporting/applications/dns.json').json

        LOG.debug "dns applications: $applications.size"
        applications.each { LOG.debug "$it" }

        assert !applications.isEmpty()

        def application = applications[0]
        assert Integer.valueOf(application.id) != null
        assert application.name != null
    }

    @Test
    void testListHttpApplications() {
        def applications = api.get('/v2/config/applications/http.json').json

        LOG.debug "http applications: $applications.size"
        applications.each { LOG.debug "$it" }

        assert !applications.isEmpty()

        def application = applications[0]
        assert Integer.valueOf(application.id) != null
        assert application.name != null
    }

    @Test
    void testListContinents() {
        def continents = api.get('/v2/reporting/continents.json').json

        LOG.debug "continents: $continents.size"
        continents.each { LOG.debug "$it" }

        assert 6 == continents.size()

        assert continents.find { it.name == 'Americas' }
        assert continents.find { it.name == 'Unknown' }
        assert continents.find { it.name == 'Africa' }
        assert continents.find { it.name == 'Europe' }
        assert continents.find { it.name == 'Asia' }
        assert continents.find { it.name == 'Oceania' }
    }

    @Test
    void testListSubcontinents() {
        def subcontinents = api.get('/v2/reporting/subcontinents.json').json

        LOG.debug "subcontinents: $subcontinents.size"
        subcontinents.each { LOG.debug "$it" }

        assert subcontinents.size() > 20

        assert subcontinents.find { it.name == 'Middle Africa' }
        assert subcontinents.find { it.name == 'Northern America' }
        assert subcontinents.find { it.name == 'South-Eastern Asia' }
    }

    @Test
    void testListCountries() {
        def countries = api.get('/v2/reporting/countries.json').json

        printAll countries

        assert countries.size() > 200

        def france = countries.find { it.name == 'France' }
        assert 'FR' == france.isoCode
        assert 'France' == france.name
        assert 'Western Europe' == france.subcontinent.name
        assert 'Europe' == france.subcontinent.continent.name
    }

    @Test
    void testListCustomers() {
        def customers = api.get('/v2/reporting/customers.json').json

        LOG.debug "customers: $customers.size"
        customers.each { LOG.debug "$it" }

        assert 1 == customers.size()
    }

    @Test
    void testListNetworks() {
        def networks = api.get('/v2/reporting/networks.json', [q: 'comcast']).json

        LOG.debug "networks: $networks.size"
        networks.each { LOG.debug "$it" }

        assert networks.size() > 10
        networks.each { assert it.name.toLowerCase() =~ 'comcast' }
    }

    @Test
    void testListProbeTypes() {
        def probeTypes = api.get('/v2/reporting/probetypes.json').json

        LOG.debug "probe types: $probeTypes.size"
        probeTypes.each { LOG.debug "$it" }

        assert probeTypes.size() > 40

        def availability = probeTypes.find { it.name == 'Availability' }
        assert 3 == availability.id
        assert 'Availability' == availability.name
        assert 'AVAIL' == availability.measurement
        assert 'HTTP' == availability.protocol
    }

    @Test
    void testListProbeTypesUsesEnglishForUnsupportedLocale() {
        api.acceptLanguage = 'cr' // Croation

        def probeTypes = api.get('/v2/reporting/probetypes.json').json

        def availability = probeTypes.find { it.name == 'Availability' }
        assert 3 == availability.id
        assert 'Availability' == availability.name
        assert 'AVAIL' == availability.measurement
        assert 'HTTP' == availability.protocol
    }

    @Test
    void testListUserAgents() {
        def userAgents = api.get('/v2/reporting/useragents.json', [q: null]).json

        LOG.debug "userAgents: $userAgents.size"
        userAgents.each { LOG.debug "$it" }

        assert userAgents.size() > 150

        assert userAgents.find { it.browserFamily.name == 'MSIE' }
        assert userAgents.find { it.browserFamily.name == 'Chrome' }
        assert userAgents.find { it.os.name == 'Windows' }
        assert userAgents.find { it.majorVersion.name == '22' }
    }

    @Test
    @Ignore("user agent searching is not currently supported")
    void testListUserAgentsWithSearch() {
        def userAgents = api.get('/v2/reporting/useragents.json', [q: 'chrome']).json

        LOG.debug "userAgents: $userAgents.size"
        userAgents.each { LOG.debug "$it" }

        assert 40 < userAgents.size()
        assert 60 > userAgents.size()

        assert !userAgents.find { it.agentName == 'IE' }
        assert userAgents.find { it.agentName == 'Chrome' }
        assert userAgents.find { it.os == 'Windows' }
        assert userAgents.find { it.majorVersion == '22' }
        userAgents.each { assert it.agentName == 'Chrome' }
    }

    @Test
    void testListStatistics() {
        def statistics = api.get('/v2/reporting/statistics.json').json

        LOG.debug "statistics: $statistics.size"
        statistics.each { LOG.debug "$it" }

        assert 9 == statistics.size()

        def measurements = statistics.find { it.name == 'measurements' }
        assert 'measurements' == measurements.name
        assert 'Measurements' == measurements.description
    }

    @Test
    void testListStatisticsIsFrenchLocaleAware() {
        api.acceptLanguage = 'fr'

        def statistics = api.get('/v2/reporting/statistics.json').json

        def measurements = statistics.find { it.name == 'measurements' }
        assert 'Mesures' == measurements.description
    }

    @Test
    void testListPlatforms() {
        //
        // just community platforms
        def communityPlatforms = api.get('/v2/reporting/platforms.json/community').json

        LOG.debug "community platforms: $communityPlatforms.size"
        communityPlatforms.each { LOG.debug "$it" }

        assert communityPlatforms.size() > 50
        assert communityPlatforms.findAll { it.name.startsWith('AWS') }.size() > 5
        communityPlatforms.each {
            assert it.visibility == 'community'
            assert it.aliasedCommunityPlatformId == null
        }

        //
        // just private platforms
        def privatePlatforms = api.get('/v2/reporting/platforms.json/private').json

        LOG.debug "private platforms: $privatePlatforms.size"
        privatePlatforms.each { LOG.debug "$it" }
        assert privatePlatforms
        privatePlatforms.each {
            assert it.visibility == 'private'
        }

        //
        // community and private platforms
        def allPlatforms = api.get('/v2/reporting/platforms.json').json

        LOG.debug "all platforms: $allPlatforms.size"
        allPlatforms.each { LOG.debug "$it" }
        assert allPlatforms.size() == communityPlatforms.size() + privatePlatforms.size()
    }

    @Test
    void testListAddAndLoadReferers() {
        //
        // add a referer
        def referers = api.get('/v2/reporting/referers.json').json
        def preAddRefererCount = referers.size()

        def url = 'www.test.com/' + UUID.randomUUID().toString()
        def addedReferer = api.post('/v2/config/referers.json', [url: url]).json

        LOG.debug "added referer: $addedReferer"

        //
        // list referers
        referers = api.get('/v2/reporting/referers.json').json

        LOG.debug "referers: $referers.size"
        referers.each { LOG.debug "$it" }

        assert 1 + preAddRefererCount == referers.size()
        assert referers.find { it.url.startsWith('www.test.com/') }

        assert url == addedReferer.url
        assert addedReferer.id

        //
        // load a referer
        def loadedReferer = api.get('/v2/config/referers.json/' + addedReferer.id).json
        assert loadedReferer.id == addedReferer.id
        assert loadedReferer.url == addedReferer.url
    }

    @Test
    void testLoadRefererNotFound() {
        try {
            api.get '/v2/config/referers.json/' + -1
        } catch (ApiException e) {
            assert 404 == e.errorResponse.httpStatus
        }
    }

}