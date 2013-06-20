package com.cedexis.api.v2.sampleclient.cli

import com.beust.jcommander.JCommander
import com.beust.jcommander.ParameterException
import com.cedexis.api.v2.sampleclient.CedexisApi
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * This is the command line interface for the Cedexis Api sample client.  Command line options
 * can be obtained by running:
 * <pre>
 * ./apiclient --help
 * </pre>
 *
 * @author jon
 */
class Main {

    // -------------------------------------------------------------------------
    // private member variables
    // -------------------------------------------------------------------------
    private static final Logger LOG = LoggerFactory.getLogger(Main)
    private static final String BAR = '--------------------------------------------------'
    private static final int BAR_LENGTH = BAR.size()

    private static CedexisApi api

    private static final COMMANDS = [
            new Commands.WelcomeCommand(),
            new Commands.ExitCommand(),
            new Commands.PingCommand(),
            new Commands.ListDnsApplicationsCommand(),
            new Commands.ListHttpApplicationsCommand(),
            new Commands.ListContinentsCommand(),
            new Commands.ListSubcontinentsCommand(),
            new Commands.ListCountriesCommand(),
            new Commands.ListCustomersCommand(),
            new Commands.ListProbeTypesCommand(),
            new Commands.SearchUserAgentsCommand(),
            new Commands.ListStatisticsCommand(),
            new Commands.SearchProvidersCommand(),
            new Commands.SearchNetworksCommand(),
            new Commands.ListReferrersCommand(),
            new Commands.AddAReferrerCommand()
    ]

    // -------------------------------------------------------------------------
    // constructors
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // methods from XXX
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // private methods
    // -------------------------------------------------------------------------
    /**
     * Parse the command line. Log any problems and exit if the command line is
     * invalid.
     */
    private static CommandLine parseCommandLine(String[] args) {
        CommandLine cl = new CommandLine()

        try {

            JCommander jCommander = new JCommander(cl, args)
            jCommander.programName = 'apiclient'

            if (cl.help) {
                jCommander.usage()
                exit(false)
            }

        } catch (ParameterException e) {
            logAndExit(e)
        }

        cl
    }

    private static void logAndExit(ParameterException e) {
        String msg = e.message
        LOG.warn 'Use --help for more information'
        LOG.warn msg
        exit true
    }

    private static void exit(boolean withError) {
        System.exit(withError ? 1 : 0)
    }

    private static sep(msg) {
        LOG.info BAR
        LOG.info msg.center(BAR_LENGTH, '-')
        LOG.info BAR
    }

    private static setUp(uri, clientId, clientSecret) {
        api = new CedexisApi(uri, clientId, clientSecret)
    }

    private static loop() {
        while (true) {
            printCommands()
            int cmd = readCommand()
            LOG.info 'Executing...'.center(BAR_LENGTH, '-')
            try {
                COMMANDS[cmd].process(api)
            } catch (Exception e) {
                LOG.error "ERROR: $e"

            }
            LOG.info 'Complete'.center(BAR_LENGTH, '-')
        }
    }

    private static printCommands() {
        sep 'Command - Choose One'
        COMMANDS.eachWithIndex { c, i ->
            LOG.info i + ' : ' + c.name()
        }
    }

    private static readCommand() {
        try {
            LOG.info 'Enter Command #: '
            BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in))
            int choice = Integer.valueOf(stdin.readLine())
            if (choice >= COMMANDS.size()) { return 0 }
            choice
        } catch (Exception e) {
            0
        }
    }

    // -------------------------------------------------------------------------
    // public methods
    // -------------------------------------------------------------------------
    static void main(String[] args) {
        CommandLine commandLine = parseCommandLine(args)
        setUp(commandLine.uri, commandLine.clientId, commandLine.clientSecret)
        loop()
    }

    // -------------------------------------------------------------------------
    // getters/setters
    // -------------------------------------------------------------------------
}
