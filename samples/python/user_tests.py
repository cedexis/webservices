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



# get the list of users associated with this customer
url = "https://%s/cedexis_ws/user.json" % portal
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

# load a single user, by id
url = "https://%s/cedexis_ws/user/%s.json" % (portal, j['users'][-1]['id'])
client = oauth.Client(consumer, token)
resp, content = client.request(url, "GET")
if resp['status'] != '200':
    print("Invalid response %s." % resp['status'], content)
else:
    print url,":"
    io = StringIO(content)
    j = json.load(io)
    print json.dumps(j, sort_keys=True, indent=4)
    print

# create a new user.  Takes only an email param, via POST
url = "https://%s/cedexis_ws/user.json" % portal
client = oauth.Client(consumer, token)
resp, content = client.request(url, 
                               "POST", 
                               realm="CedexisRealm", 
                               body="email=sam@cedexis.com\r\n\r\n")
if resp['status'] != '200':
    print("Invalid response %s." % resp['status'], content)
else:
    print url,":"
    io = StringIO(content)
    j = json.load(io)
    print json.dumps(j, sort_keys=True, indent=4)
    print

# DELETE a user by id. Note, there is currently no method for updating a 
# user in the web service
url = "https://%s/cedexis_ws/user/%s.json" % (portal, j['user']['id'])
client = oauth.Client(consumer, token)
resp, content = client.request(url, "DELETE", realm="CedexisRealm")
if resp['status'] != '200':
    print("Invalid response %s." % resp['status'], content)
else:
    print url,":"
    io = StringIO(content)
    j = json.load(io)
    print json.dumps(j, sort_keys=True, indent=4)
    print

