#!/bin/bash

#
# Configures Maven publish settings for local development.  See ./publish.configure for details.
#
# Attempts to source default configuration parameters from ~/cedexis_webservices_publish_defaults.sh
# Add the following environment variables to that file to avoid having to pass them on the command line:
#
# #!/bin/bash
# CEDEXISAPITEST_URI=https://...../api
# CEDEXISAPITEST_CLIENT_ID=<test-client-id>
# CEDEXISAPITEST_CLIENT_SECRET=<test-client-secret>
#

CONFIG_DEFAULTS=~/cedexis_webservices_publish_defaults.sh

BASE_DIR=$SCRIPT_DIR/..


# Load defaults
. $CONFIG_DEFAULTS