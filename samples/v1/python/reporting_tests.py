#!/usr/bin/python
import oauth2 as oauth
import urlparse
import json
from StringIO import StringIO
import mimetypes, mimetools
import urllib
import sys

if len(sys.argv) < 6:
    print "%s <hostname> <consumer key> <consumer secret> <access token id> <access token secret>" % sys.argv[0]
    sys.exit(-1)

# The hostname for accessing the web service
#portal = "portal.cedexis.com"
portal = sys.argv[1]

if portal.isspace() or len(portal) == 0:
    print "%s <hostname> <consumer key> <consumer secret> <access token id> <access token secret>" % sys.argv[0]
    sys.exit(-1)

if sys.argv[2].isspace() or len(sys.argv[2]) == 0:
    print "%s <hostname> <consumer key> <consumer secret> <access token id> <access token secret>" % sys.argv[0]
    sys.exit(-1)

if sys.argv[3].isspace() or len(sys.argv[3]) == 0:
    print "%s <hostname> <consumer key> <consumer secret> <access token id> <access token secret>" % sys.argv[0]
    sys.exit(-1)

if sys.argv[4].isspace() or len(sys.argv[4]) == 0:
    print "%s <hostname> <consumer key> <consumer secret> <access token id> <access token secret>" % sys.argv[0]
    sys.exit(-1)

if sys.argv[5].isspace() or len(sys.argv[5]) == 0:
    print "%s <hostname> <consumer key> <consumer secret> <access token id> <access token secret>" % sys.argv[0]
    sys.exit(-1)

consumer = oauth.Consumer(sys.argv[2],
                          sys.argv[3])

token = oauth.Token(sys.argv[4], sys.argv[5])

resource_urls      = [
    "https://%s/cedexis_ws/report/probetype.json" % portal,
    "https://%s/cedexis_ws/report/market.json" % portal,
    "https://%s/cedexis_ws/report/country.json?market=3" % portal,
    "https://%s/cedexis_ws/report/autosys.json?country=223" % portal,
# specify locale=fr or locale=en in any url in order to force the language
# of any messages or labels in the response
    "https://%s/cedexis_ws/report/timescale.json?locale=fr" % portal,
    "https://%s/cedexis_ws/report/provider.json" % portal,
    "https://%s/cedexis_ws/report/application.json" % portal,
    "https://%s/cedexis_ws/report/chart.json" % portal
]

# iterate over the read-only chart filter urls, grabbing each one
for url in resource_urls:
    client = oauth.Client(consumer, token)
    # it is also possible to issue POST requests along with a request body
    # instead of query string.  But not all API calls accept POST requests.
    # To issue PUT and DELETE calls, you can POST with a body param of
    # _method=PUT or _method=DELETE if your client doesn't support PUT and 
    # DELETE. This is also how one would handle those methods from a web 
    # browser, since none of them support PUT or DELETE, either.  Some cilents
    # incorrectly handle the request and response body in PUT and DELETE
    # calls, too.
    resp, content = client.request(url, "GET")

    if resp['status'] != '200':
        print("Invalid response %s." % resp['status'])
    else:
        print url,":"
        io = StringIO(content)
        j = json.load(io)
        print json.dumps(j, sort_keys=True, indent=4)
        print

# load a chart's data
url = "https://%s/cedexis_ws/report/data.json?chart=radar.requests.by-location.pie&timescale=H&market=3&country=70&probetype=0&providers=10013-564&providers=10013-634&providers=10013-600&providers=0-13&providers=0-287&providers=0-33&providers=0-24" % portal
#url = "https://%s/cedexis_ws/report/data.json?chart=radar.requests.by-location.pie" % portal
client = oauth.Client(consumer, token)
resp, content = client.request(url, "GET")
if resp['status'] != '200':
    print "Invalid response %s." % resp['status'], content
else:
    print url,":"
    io = StringIO(content)
    j = json.load(io)
    print json.dumps(j, sort_keys=True, indent=4)
    print


