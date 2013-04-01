#!/bin/bash

#
# Configures Maven publish settings.
#
# To use the local configuration instead of the deployment (default) configuration,
# set the following environment variable:
#
# export CDX_ENV=local
#
# You can set this environment variable in ~/.profile so that it's always set on your machine,
# otherwise you can just set it on the command line using the above statement if you
# don't want to set it permanently in ~/.profile.
#
# To automatically use the Bamboo configuration, set CDX_ENV=bamboo:
#
# export CDX_ENV=bamboo
#

SCRIPT_DIR=`dirname $0`


echo "CDX_ENV is set to ${CDX_ENV}"

if [ "${CDX_ENV}" = "local" ]; then
    echo "Configuring Publishing for local development"
    . `dirname $0`/publish.configure.local.sh
else
    echo "Configuring Publishing for Bamboo deployment"
    . `dirname $0`/publish.configure.bamboo.sh
fi

echo "CEDEXISAPITEST_URI: ${CEDEXISAPITEST_URI}, CEDEXISAPITEST_CLIENT_ID: ${CEDEXISAPITEST_CLIENT_ID}, CEDEXISAPITEST_CLIENT_SECRET: ********"

# Fail if defaults are not defined
if [ -z "$CEDEXISAPITEST_URI" ]; then
    echo "ERROR: CEDEXISAPITEST_URI environment variable is required"
    exit 1
fi

if [ -z "$CEDEXISAPITEST_CLIENT_ID" ]; then
    echo "ERROR: CEDEXISAPITEST_CLIENT_ID environment variable is required"
    exit 1
fi

if [ -z "$CEDEXISAPITEST_CLIENT_SECRET" ]; then
    echo "ERROR: CEDEXISAPITEST_CLIENT_SECRET environment variable is required"
    exit 1
fi