package com.cedexis.api.v2.sampleclient.cli
import com.cedexis.api.v2.sampleclient.CedexisApi
import org.slf4j.Logger
import org.slf4j.LoggerFactory
/**
 * The various commands that the sample app implements.
 *
 * @author jon
 */
final class Commands {

    private static final Logger LOG = LoggerFactory.getLogger(Commands)

    private Commands() { }

    static abstract class AbstractCommand implements Command {
        @Override
        void process(CedexisApi api) {
            LOG.info name() + '...'
            doProcess(api)
        }

        protected abstract void doProcess(CedexisApi api)

        protected static String readStdIn(prompt) {
            try {
                LOG.info prompt + ': '
                BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in))
                stdin.readLine()
            } catch (Exception e) {
                LOG.info "$e"
                null
            }
        }
    }

    static class WelcomeCommand implements Command {
        @Override
        String name() { 'Welcome' }

        @Override
        void process(CedexisApi api) { LOG.info 'Welcome!' }
    }

    static class ExitCommand extends AbstractCommand {
        @Override
        String name() { 'Exit' }

        @Override
        void doProcess(CedexisApi api) { System.exit(0) }
    }

    static class PingCommand extends AbstractCommand {
        @Override
        String name() { 'Ping and API Version Check' }

        @Override
        void doProcess(CedexisApi api) {
            LOG.info 'Ping: ' + api.get('/v2/meta/system.json/ping').json.result
            LOG.info 'API version: ' + api.get('/v2/meta/system.json/version').json.result
        }
    }

    static class ListDnsApplicationsCommand extends AbstractCommand {
        @Override
        String name() { 'List DNS Applications' }

        @Override
        void doProcess(CedexisApi api) { api.get('/v2/reporting/applications/dns.json').json.each { LOG.info "$it" } }
    }

    static class ListHttpApplicationsCommand extends AbstractCommand {
        @Override
        String name() { 'List HTTP Applications' }

        @Override
        void doProcess(CedexisApi api) { api.get('/v2/config/applications/http.json').json.each { LOG.info "$it" } }
    }

    static class ListContinentsCommand extends AbstractCommand {
        @Override
        String name() { 'List Continents' }

        @Override
        void doProcess(CedexisApi api) { api.get('/v2/reporting/continents.json').json.each { LOG.info "$it" } }
    }

    static class ListSubcontinentsCommand extends AbstractCommand {
        @Override
        String name() { 'List Subcontinents' }

        @Override
        void doProcess(CedexisApi api) { api.get('/v2/reporting/subcontinents.json').json.each { LOG.info "$it" } }
    }

    static class ListCountriesCommand extends AbstractCommand {
        @Override
        String name() { 'List Countries' }

        @Override
        void doProcess(CedexisApi api) { api.get('/v2/reporting/countries.json').json.each { LOG.info "$it" } }
    }

    static class ListCustomersCommand extends AbstractCommand {
        @Override
        String name() { 'List Customers' }

        @Override
        void doProcess(CedexisApi api) { api.get('/v2/reporting/customers.json').json.each { LOG.info "$it" } }
    }

    static class ListProbeTypesCommand extends AbstractCommand {
        @Override
        String name() { 'List Probe Types' }

        @Override
        void doProcess(CedexisApi api) { api.get('/v2/reporting/probetypes.json').json.each { LOG.info "$it" } }
    }

    static class SearchUserAgentsCommand extends AbstractCommand {
        @Override
        String name() { 'List User Agents' }

        @Override
        void doProcess(CedexisApi api) { api.get('/v2/reporting/useragents.json').json.each { LOG.info "$it" } }
    }

    static class ListStatisticsCommand extends AbstractCommand {
        @Override
        String name() { 'List Statistics' }

        @Override
        void doProcess(CedexisApi api) { api.get('/v2/reporting/statistics.json').json.each { LOG.info "$it" } }
    }

    static class SearchPlatformsCommand extends AbstractCommand {
        @Override
        String name() { 'Search Platforms' }

        @Override
        void doProcess(CedexisApi api) {
            def q = readStdIn 'Enter "community" or "private"'
            LOG.info "Searching for $q platforms..."
            def platforms = api.get("/v2/reporting/platforms.json/$q").json
            platforms.each { LOG.info "$it" }
            LOG.info "$platforms.size platforms found"
        }
    }

    static class SearchNetworksCommand extends AbstractCommand {
        @Override
        String name() { 'Search Networks' }

        @Override
        void doProcess(CedexisApi api) {
            def q = readStdIn 'Enter network name'
            LOG.info "Searcing for networks named like [$q]..."
            def networks = api.get('/v2/reporting/networks.json', [q: q]).json
            networks.each { LOG.info "$it" }
            LOG.info "$networks.size networks found"
        }
    }

    static class ListReferersCommand extends AbstractCommand {
        @Override
        String name() { 'List Referers' }

        @Override
        void doProcess(CedexisApi api) { api.get('/v2/reporting/referers.json').json.each { LOG.info "$it" } }
    }

    static class AddARefererCommand extends AbstractCommand {
        @Override
        String name() { 'Add Referer' }

        @Override
        void doProcess(CedexisApi api) {
            def q = readStdIn 'Enter a URL'
            LOG.info "Adding '[$q]' as a referer..."
            def added = api.post('/v2/config/referers.json', [url: q]).json
            LOG.info "Added $added"
        }
    }
}
