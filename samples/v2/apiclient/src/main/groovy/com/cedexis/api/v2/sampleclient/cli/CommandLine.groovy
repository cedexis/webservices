package com.cedexis.api.v2.sampleclient.cli

import com.beust.jcommander.Parameter

/**
 * Codifies the args that can be passed in via command line.
 *
 * @author jon
 */
class CommandLine {
    @Parameter(names = ['--uri'], description = 'Cedexis API URI')
    String uri = 'https://portal.cedexis.com/api'

    @Parameter(names = ['--client_id'], description = 'OAuth 2.0 client_id', required = true)
    String clientId

    @Parameter(names = ['--client_secret'], description = 'OAuth 2.0 client_secret', required = true)
    String clientSecret

    @Parameter(names = ['--help', '-h'], help = true, description = 'Print this help message')
    boolean help

}
