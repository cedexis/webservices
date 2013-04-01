#!/bin/bash
. `dirname $0`/publish.configure.sh

# ##############################################################################
# Publishes artifacts to the RELEASE Maven repository.
#
# Publishes to the SNAPSHOT repo if the version contains the string "SNAPSHOT",
# otherwise publishes to the RELEASE repo
# ##############################################################################

gradle \
    -DCedexisApiTest.uri=$CEDEXISAPITEST_URI \
    -DCedexisApiTest.client_id=$CEDEXISAPITEST_CLIENT_ID \
    -DCedexisApiTest.client_secret=$CEDEXISAPITEST_CLIENT_SECRET \
    samples:v2:apiclient:uploadArchives