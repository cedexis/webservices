#!/usr/bin/python
import oauth2 as oauth
import urlparse
import json
from StringIO import StringIO
import mimetypes, mimetools
import urllib
import sys

def print_usage():
    print "%s <hostname> <consumer key> <consumer secret> [<access token id> <access token secret>]" % sys.argv[0]

def print_banner(banner):
    print "-----------------------------------------------------------------"
    print "    %s" % banner                
    print "-----------------------------------------------------------------"

if len(sys.argv) < 4 or len(sys.argv) == 5:
    print_usage()
    sys.exit(-1)

# The hostname for accessing the web service
#portal = "portal.cedexis.com"
portal = sys.argv[1]

if portal.isspace() or len(portal) == 0:
    print_usage()
    sys.exit(-1)

if sys.argv[2].isspace() or len(sys.argv[2]) == 0:
    print_usage()
    sys.exit(-1)

if sys.argv[3].isspace() or len(sys.argv[3]) == 0:
    print_usage()
    sys.exit(-1)

if len(sys.argv) > 4:
    if sys.argv[4].isspace() or len(sys.argv[4]) == 0:
        print_usage()
        sys.exit(-1)

    if sys.argv[5].isspace() or len(sys.argv[5]) == 0:
        print_usage()
        sys.exit(-1)

    token = oauth.Token(sys.argv[4], sys.argv[5])
else:
    token = None

consumer = oauth.Consumer(sys.argv[2],
                          sys.argv[3])


testNum = 0

print_banner("Cedexis Product Feature Tests")


testNum += 1
print_banner("Test %d: List Current Radar Features " % testNum)
url = "https://%s/cedexis_ws/feature/radar.json" % portal
client = oauth.Client(consumer, token)
resp, content = client.request(url, "GET")
if resp['status'] != '200':
    print("Invalid response %s." % resp['status'])
else:
    print url,":"
    io = StringIO(content)
    j = json.load(io)
    print json.dumps(j, sort_keys=True, indent=4)
    print


testNum += 1
print_banner("Test %d: Update Radar Features " % testNum)
url = "https://%s/cedexis_ws/feature/radar.json" % portal
radarFeatures = {
	'radarStandardEnabled': True,
	'secureEnabled': True,
	'streamingEnabled': True
}
radarFeatures['_method'] = "PUT"
body = urllib.urlencode(radarFeatures, True)
client = oauth.Client(consumer, token)
resp, content = client.request(url, 
                               "POST", 
                               realm="CedexisRealm", 
                               body=body)
if resp['status'] != '200':
    print("Invalid response %s." % resp['status'], content)
else:
    print url,":"
    io = StringIO(content)
    j = json.load(io)
    print json.dumps(j, sort_keys=True, indent=4)
    print


testNum += 1
print_banner("Test %d: List Current OpenMix Features " % testNum)
url = "https://%s/cedexis_ws/feature/openmix.json" % portal
client = oauth.Client(consumer, token)
resp, content = client.request(url, "GET")
if resp['status'] != '200':
    print("Invalid response %s." % resp['status'])
else:
    print url,":"
    io = StringIO(content)
    j = json.load(io)
    print json.dumps(j, sort_keys=True, indent=4)
    print


testNum += 1
print_banner("Test %d: Update OpenMix Features " % testNum)
url = "https://%s/cedexis_ws/feature/openmix.json" % portal
openMixFeatures = {
	'openMixEnabled': True,
	'openMixPulseDatafeedEnabled': True,
	'openMixfusionEnabled': True
}
openMixFeatures['_method'] = "PUT"
body = urllib.urlencode(openMixFeatures, True)
client = oauth.Client(consumer, token)
resp, content = client.request(url, 
                               "POST", 
                               realm="CedexisRealm", 
                               body=body)
if resp['status'] != '200':
    print("Invalid response %s." % resp['status'], content)
else:
    print url,":"
    io = StringIO(content)
    j = json.load(io)
    print json.dumps(j, sort_keys=True, indent=4)
    print


testNum += 1
print_banner("Test %d: List Current Fusion Features " % testNum)
url = "https://%s/cedexis_ws/feature/fusion.json" % portal
client = oauth.Client(consumer, token)
resp, content = client.request(url, "GET")
if resp['status'] != '200':
    print("Invalid response %s." % resp['status'])
else:
    print url,":"
    io = StringIO(content)
    j = json.load(io)
    print json.dumps(j, sort_keys=True, indent=4)
    print


testNum += 1
print_banner("Test %d: Update Fusion Features " % testNum)
url = "https://%s/cedexis_ws/feature/fusion.json" % portal
fusionFeatures = {
	'fusionDatafeedEnabled': True,
	'fusionPurgeEnabled': True,
	'fustionReportingEnabled': True
}
fusionFeatures['_method'] = "PUT"
body = urllib.urlencode(radarFeatures, True)
client = oauth.Client(consumer, token)
resp, content = client.request(url, 
                               "POST", 
                               realm="CedexisRealm", 
                               body=body)
if resp['status'] != '200':
    print("Invalid response %s." % resp['status'], content)
else:
    print url,":"
    io = StringIO(content)
    j = json.load(io)
    print json.dumps(j, sort_keys=True, indent=4)
    print

sys.exit(0)

