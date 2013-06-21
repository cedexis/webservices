#!/usr/bin/env python
from OAuthApiTest import OAuthApiTest, encode_multipart_formdata

apiTest = OAuthApiTest("OAuth Application Tests")

j = apiTest.doGetTest("Test %d: List all applications for customer", "https://%s/cedexis_ws/application.json")

app = j['apps'][-1]
apiTest.doGetTest("Test %d: Fetch a single application by id.", "https://%s" + "/cedexis_ws/application/%s.json" % app['id'])

# regular fields for our new app
fields = [("displayName","web services test"),
          ("description", "uploaded via web service"),
          ("defaultCname","ws.cname")]
# the body of our file and a filename.  filename is actually ignored by web service
files = [("data", 
         "ws_test.php", 
         """<?php
class OpenmixApplication implements Lifecycle {
    public function init($config) {}
    public function service($request,$response,$utilities) {}
}
?>""")]

j = apiTest.doMultipartFormPostTest("Test %d: Create App", "https://%s/cedexis_ws/application.json", fields, files)

app = j['app']
# modified fields
fields = [("displayName",app['displayName'] + " next"),
          ("description", app["description"] + " next"),
          ("defaultCname",app['defaultCname'] + ".next")]
(content_type, body) = encode_multipart_formdata(fields, files)

# normally, updating an object would involve PUT in a REST environment, but
# a bug requires the use of POST for all multipart mime formatted requests.
# The bug makes it impossible to use the _method=PUT trick, so we POST to
# the application's id, since application creation POSTs to /application
j = apiTest.doMultipartFormPostTest("Test %d: Update App", "https://%s" + "/cedexis_ws/application/%d.json" % app['id'], fields, files)

# DELETE the app we loaded then updated, by id
j = apiTest.doDeleteTest("Test %d: Delete App", "https://%s" + "/cedexis_ws/application/%d.json" % app['id'])

