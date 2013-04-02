package com.cedexis.api.v2.sampleclient
import org.junit.Before
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
        if (!value) { throw new IllegalArgumentException("$key is required (try '-D$key=...' on the command line") }
        value
    }

    @Test
    void testPing() {
        Date datetime = api.ping()
        LOG.debug "datetime: $datetime"
        assert datetime != null
    }

    @Test
    void testApiVersion() {
        def apiVersion = api.apiVersion()
        LOG.debug "apiVersion: $apiVersion"
        assert 'v2' == apiVersion
    }

    @Test
    void testListApplications() {
        def applications = api.listApplications()

        LOG.debug "applications: $applications.size"
        applications.each { LOG.debug "$it" }

        assert !applications.isEmpty()

        def application = applications[0]
        assert Integer.valueOf(application.id) != null
        assert application.name != null
    }

    @Test
    void testListContinents() {
        def continents = api.listContinents()

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
        def subcontinents = api.listSubcontinents()

        LOG.debug "subcontinents: $subcontinents.size"
        subcontinents.each { LOG.debug "$it" }

        assert subcontinents.size() > 20

        assert subcontinents.find { it.name == 'Middle Africa' }
        assert subcontinents.find { it.name == 'Northern America' }
        assert subcontinents.find { it.name == 'South-Eastern Asia' }
    }

    @Test
    void testListCountries() {
        def countries = api.listCountries()

        LOG.debug "countries: $countries.size"
        countries.each { LOG.debug "$it" }

        assert countries.size() > 100

        def france = countries.find { it.name == 'France' }
        assert 'FR' == france.code
        assert 'France' == france.name
        assert 'Western Europe' == france.subcontinentName
        assert 'Europe' == france.continentName
    }

    @Test
    void testListCustomers() {
        def customers = api.listCustomers()

        LOG.debug "customers: $customers.size"
        customers.each { LOG.debug "$it" }

        assert 1 == customers.size()
    }

    @Test
    void testListNetworks() {
        def networks = api.listNetworks('comcast')

        LOG.debug "networks: $networks.size"
        networks.each { LOG.debug "$it" }

        assert networks.size() > 10
        networks.each { assert it.name.toLowerCase() =~ 'comcast' }
    }

    @Test
    void testListProbeTypes() {
        def probeTypes = api.listProbeTypes()

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
    void testListProbeTypesIsFrenchLocaleAware() {
        api.acceptLanguage = 'fr'

        def probeTypes = api.listProbeTypes()

        def availability = probeTypes.find { it.id == 3 }
        assert 3 == availability.id
        assert 'Disponibilité' == availability.name
        assert 'AVAIL' == availability.measurement
        assert 'HTTP' == availability.protocol
    }

    @Test
    void testListProbeTypesUsesEnglishForUnsupportedLocale() {
        api.acceptLanguage = 'cr' // Croation

        def probeTypes = api.listProbeTypes()

        def availability = probeTypes.find { it.name == 'Availability' }
        assert 3 == availability.id
        assert 'Availability' == availability.name
        assert 'AVAIL' == availability.measurement
        assert 'HTTP' == availability.protocol
    }

    @Test
    void testListUserAgents() {
        def userAgents = api.listUserAgents(null)

        LOG.debug "userAgents: $userAgents.size"
        userAgents.each { LOG.debug "$it" }

        assert userAgents.size() > 150

        assert userAgents.find { it.agentName == 'Internet Explorer' }
        assert userAgents.find { it.agentName == 'Chrome' }
        assert userAgents.find { it.os == 'Windows' }
        assert userAgents.find { it.majorVersion == '22' }
    }

    @Test
    void testListUserAgentsWithSearch() {
        def userAgents = api.listUserAgents('chrome')

        LOG.debug "userAgents: $userAgents.size"
        userAgents.each { LOG.debug "$it" }

        assert 40 < userAgents.size()
        assert 60 > userAgents.size()

        assert !userAgents.find { it.agentName == 'Internet Explorer' }
        assert userAgents.find { it.agentName == 'Chrome' }
        assert userAgents.find { it.os == 'Windows' }
        assert userAgents.find { it.majorVersion == '22' }
        userAgents.each { assert it.agentName == 'Chrome' }
    }

    @Test
    void testListStatistics() {
        def statistics = api.listStatistics()

        LOG.debug "statistics: $statistics.size"
        statistics.each { LOG.debug "$it" }

        assert 8 == statistics.size()

        def measurements = statistics.find { it.name == 'measurements' }
        assert 'measurements' == measurements.name
        assert 'Measurements' == measurements.description
    }

    @Test
    void testListProviders() {
        //
        // just community providers
        def communityProviders = api.listProviders('COMMUNITY')

        LOG.debug "community providers: $communityProviders.size"
        communityProviders.each { LOG.debug "$it" }

        assert communityProviders.size() > 50
        assert communityProviders.findAll { it.name.startsWith('Amazon') }.size() > 5
        communityProviders.each {
            assert it.visibility == 'COMMUNITY'
            assert it.aliasedCommunityProviderId == null
        }

        //
        // just private providers
        def privateProviders = api.listProviders('PRIVATE')

        LOG.debug "private providers: $privateProviders.size"
        privateProviders.each { LOG.debug "$it" }
        assert privateProviders
        privateProviders.each {
            assert it.visibility == 'PRIVATE'
            assert it.aliasedCommunityProviderId != null
        }

        //
        // community and private providers
        def allProviders = api.listProviders(null)

        LOG.debug "all providers: $allProviders.size"
        allProviders.each { LOG.debug "$it" }
        assert allProviders.size() == communityProviders.size() + privateProviders.size()
    }

    @Test
    void testListAddAndLoadReferrers() {
        //
        // add a referrer
        def referrers = api.listReferrers()
        def preAddReferrerCount = referrers.size()

        def url = 'http://www.test.com/' + UUID.randomUUID().toString()
        def addedReferrer = api.addReferrer url

        LOG.debug "added referrer: $addedReferrer"

        //
        // list referrers
        referrers = api.listReferrers()

        LOG.debug "referrers: $referrers.size"
        referrers.each { LOG.debug "$it" }

        assert 1 + preAddReferrerCount == referrers.size()
        assert referrers.find { it.url.startsWith('http://www.test.com/') }

        assert url == addedReferrer.url
        assert addedReferrer.id

        //
        // load a referrer
        def loadedReferrer = api.loadReferrer addedReferrer.id
        assert loadedReferrer.id == addedReferrer.id
        assert loadedReferrer.url == addedReferrer.url
    }

    @Test
    void testLoadReferrerNotFound() {
        try {
            api.loadReferrer(-1)
        } catch (ApiException e) {
            assert 404 == e.httpStatus
        }
    }

    @Test
    void testQueryRadarTrafficByCountry() {
        // View Radar measurement traffic from your visitors and the community as a whole.
//        =================================
//        :Total Radar Measurements #1
//x         inputClass=radar
//x         measurementSource=customer
//x         probeTypeId=21
//x         timeRange=1363824000%2F1364428800
//        group0Order=desc
//        group0Sort=measurements
//        group1Fact=measurements
//        group=countryId%2CtimeRange
        try {
            api.queryRadarFacts(
                    probeTypeIds: '21',
                    timeRange: 'last_5_hours',
                    // does this need to change to "customer"? here or lower in my aggapi impl?
                    measurementSource: 'myvisitors',
                    group: 'countryId,timeRange',
                    // where is group0Fact???
                    group0Sort: 'measurements',
                    group0Order: 'desc',
                    group1Fact: 'measurements'
            )
        } catch (ApiException e) {
            assert 500 == e.httpStatus
        }
//
//
//        =================================
//        :Total Radar Measurements #2
//x         inputClass=radar
//        measurementSource=customer
//        overallFact=measurements
//        probeTypeId=21
//        timeRange=1363824000%2F1364428800
//        group0Count=true
//        group0Fact=measurements
//        group0Limit=10
//        group0Offset=0
//        group0Order=desc
//        group0Sort=measurements
//        group=countryId
//        try {

//            api.queryRadarFacts(
//                    probeTypeIds: '1,2,3',
//                    providerIds: '1,2,3',
//                    countryIds: '1,2,3',
//                    networkIds: '1,2,3',
//                    timeRange: 'last_3_days',
//                    measurementSource: 'myvisitors',
//                    geoLocateUsing: 'client'
//            )
//        } catch (ApiException e) {
//            assert 500 == e.httpStatus
//        }
    }

//    @Test
//    void testQueryRadarTrafficInACountry() {
//        // How many Radar measurements are being generated in a country, network-by-network?
//
//    }
//
//    @Test
//    void testQueryRadarPerformanceByPlatform() {
//        // How do platforms compare for a selected data set?
//    }
//
//    @Test
//    void testQueryRadarVarianceByPlatform() {
//        // How does platform variance compare by platform?
//    }
//
//    @Test
//    void testQueryRadarPerformanceByCountryForAPlatform() {
//        // How does a single platform compare around the world, country-by-country?
//    }
//
//    @Test
//    void testQueryRadarVarianceByCountryForAPlatform() {
//        // How does platform variance compare by country fo ra single platform?
//    }
//
//    @Test
//    void testQueryPageLoadTimeByCountry() {
//        // Compare total and partial page load times for your visitors around the world.
//    }
//
//    @Test
//    void testQueryPageLoadTimeInACountry() {
//        // Compare total and partial page load times for your visitors across networks for individual countries.
//    }
//
//    @Test
//    void testQueryPageLoadTimeVariance() {
//        // Explore the variability in total and partial page load times for your visitors.
//    }
}