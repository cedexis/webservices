#!/bin/bash

#
# Configures Maven publish settings for Bamboo.  See ./publish.configure for details.
#
# Expects all environment variables to be specified on the command line by Bamboo.
#
# CEDEXISAPITEST_URI=https://...../api
# CEDEXISAPITEST_CLIENT_ID=<test-client-id>
# CEDEXISAPITEST_CLIENT_SECRET=<test-client-secret>
#

BASE_DIR=$SCRIPT_DIR/..