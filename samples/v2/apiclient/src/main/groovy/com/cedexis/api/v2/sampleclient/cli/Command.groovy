package com.cedexis.api.v2.sampleclient.cli

import com.cedexis.api.v2.sampleclient.CedexisApi

/**
 * Implement this to add new functionality to the sample app.
 *
 * @author jon
 */
interface Command {
    /** The name of the command - used in menus. */
    String name()

    /** Implement this to run the command. */
    void process(CedexisApi api)
}
