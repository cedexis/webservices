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
            LOG.info 'Ping: ' + api.ping()
            LOG.info 'API version: ' + api.apiVersion()
        }
    }

    static class ListApplicationsCommand extends AbstractCommand {
        @Override
        String name() { 'List Applications' }

        @Override
        void doProcess(CedexisApi api) { api.listApplications().each { LOG.info "$it" } }
    }

    static class ListContinentsCommand extends AbstractCommand {
        @Override
        String name() { 'List Continents' }

        @Override
        void doProcess(CedexisApi api) { api.listContinents().each { LOG.info "$it" } }
    }

    static class ListSubcontinentsCommand extends AbstractCommand {
        @Override
        String name() { 'List Subcontinents' }

        @Override
        void doProcess(CedexisApi api) { api.listSubcontinents().each { LOG.info "$it" } }
    }

    static class ListCountriesCommand extends AbstractCommand {
        @Override
        String name() { 'List Countries' }

        @Override
        void doProcess(CedexisApi api) { api.listCountries().each { LOG.info "$it" } }
    }

    static class ListCustomersCommand extends AbstractCommand {
        @Override
        String name() { 'List Customers' }

        @Override
        void doProcess(CedexisApi api) { api.listCustomers().each { LOG.info "$it" } }
    }

    static class ListProbeTypesCommand extends AbstractCommand {
        @Override
        String name() { 'List Probe Types' }

        @Override
        void doProcess(CedexisApi api) { api.listProbeTypes().each { LOG.info "$it" } }
    }

    static class SearchUserAgentsCommand extends AbstractCommand {
        @Override
        String name() { 'List User Agents' }

        @Override
        void doProcess(CedexisApi api) {
            def q = readStdIn 'Enter a search string'
            def userAgents = api.listUserAgents(q)
            userAgents.each { LOG.info "$it" }
        }
    }

    static class ListStatisticsCommand extends AbstractCommand {
        @Override
        String name() { 'List Statistics' }

        @Override
        void doProcess(CedexisApi api) { api.listStatistics().each { LOG.info "$it" } }
    }

    static class SearchProvidersCommand extends AbstractCommand {
        @Override
        String name() { 'Search Providers' }

        @Override
        void doProcess(CedexisApi api) {
            def q = readStdIn 'Enter COMMUNITY or PRIVATE'
            LOG.info "Searcing for $q providers..."
            api.listProviders(q).each { LOG.info "$it" }
            def providers = api.listProviders(q)
            providers.each { LOG.info "$it" }
            LOG.info "$providers.size providers found"
        }
    }

    static class SearchNetworksCommand extends AbstractCommand {
        @Override
        String name() { 'Search Networks' }

        @Override
        void doProcess(CedexisApi api) {
            def q = readStdIn 'Enter network name'
            LOG.info "Searcing for networks named like [$q]..."
            def networks = api.listNetworks(q)
            networks.each { LOG.info "$it" }
            LOG.info "$networks.size networks found"
        }
    }

    static class ListReferrersCommand extends AbstractCommand {
        @Override
        String name() { 'List Referrers' }

        @Override
        void doProcess(CedexisApi api) { api.listReferrers().each { LOG.info "$it" } }
    }

    static class AddAReferrerCommand extends AbstractCommand {
        @Override
        String name() { 'Add Referrer' }

        @Override
        void doProcess(CedexisApi api) {
            def q = readStdIn 'Enter a URL'
            LOG.info "Adding '[$q]' as a referrer..."
            def added = api.addReferrer(q)
            LOG.info "Added $added"
        }
    }
}
