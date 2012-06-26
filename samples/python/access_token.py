#!/usr/bin/python
import oauth2 as oauth
import urlparse
import json
from StringIO import StringIO
import mimetypes, mimetools
import urllib
import sys

if len(sys.argv) < 4:
    print "%s <hostname> <consumer key> <consumer secret>" % sys.argv[0]
    sys.exit(-1)

# The hostname for accessing the web service
portal = "portal.cedexis.com"
portal = sys.argv[1]
if portal.isspace() or len(portal) == 0:
    print "%s <hostname> <consumer key> <consumer secret>" % sys.argv[0]
    sys.exit(-1)

if sys.argv[2].isspace() or len(sys.argv[2]) == 0:
    print "%s <hostname> <consumer key> <consumer secret>" % sys.argv[0]
    sys.exit(-1)

if sys.argv[3].isspace() or len(sys.argv[3]) == 0:
    print "%s <hostname> <consumer key> <consumer secret>" % sys.argv[0]
    sys.exit(-1)

# The url to use to request an oauth request_token
request_token_url = "https://%s/cedexis_ws/oauth/request_token" % portal

# The url to use to request an oauth access_token after a user has 
# authorized the request_token
access_token_url  = "https://%s/cedexis_ws/oauth/access_token" % portal

# This url must have the customer-specific hostname for portal access or else
# the user will be redirected back to home.html every time they switch domains.
# With the correct domain, they'll be redirected to /home.html the first
# time they log in, but a second attempt will get them to the page
authorize_url     = "https://%s/apps/authorizeApp.html" % portal


# This consumer info will be provided to you by the Cedexis support team.  You
# need both the consumer key and secret.  Please keep the secret secure, as
# possession of it will allow anyone else to pretend to be your app.
consumer = oauth.Consumer(sys.argv[2], 
                          sys.argv[3])

token = None

client = oauth.Client(consumer)

# if the client app cannot accept a browser redirect to send the user back
# to this app, then callback_url MUST be 'oob'.  If this is a webapp which
# the portal can automatically redirect back to after authorization, then
# you shoudl specify the full url in callback_url.  The portal will 
# add an oauth_token query string element, which identifies the request
# token which was authorized, and an oauth_verifier which must be sent
# back in the request for an access token which will allow the portal
# to validate that the access token request is coming from the correct 
# app. The final callback redirect will look like this:
#     http://my.3rdparty.app/verified.html?oauth_token=106f2898-adf5-4585-b475-7aa41d848727&oauth_verifier=vlRhrs
# if the provided callback_url is: http://my.3rdparty.app/verified.html
resp, content = client.request(request_token_url, "GET", callback_url='oob')
if resp['status'] != '200':
    raise Exception("Invalid response %s." % resp['status'])

request_token = dict(urlparse.parse_qsl(content))
print "Request Token:"
print "    - oauth_token        = %s" % request_token['oauth_token']
print "    - oauth_token_secret = %s" % request_token['oauth_token_secret']
print

# Step 2: Redirect to Cedexis.  Since this is a CLI script, we do not 
# redirect. In a web application you would redirect the user to the URL 
# below, and after they authorize access, they will automatically be
# redirected back to the callback_url that was provided in the request_token
# request.  The CLI app must prompt the user to enter the oauth_verifier
print "Go to the following link in your browser:"
print "%s?oauth_token=%s" % (authorize_url, request_token['oauth_token'])
print

accepted = 'n'
while accepted.lower() == 'n':
    accepted = raw_input('Have you authorized me? (y/n) ')
oauth_verifier = raw_input("What is the PIN? ")

# Step 3: Once a consumer has redirected the user back to the oauth_callback
# URL you can request the access token the user has approved.  You use the
# request token to sign this request.  After this is done, you throw away 
# the request token and use the access token returned. You should store this
# access token somewhere safe for future use.  Do not assume that an access
# token will remain valid forever.  A user may invalidate a token by logging
# into the cedexis portal and disabling access for a 3rd party app.
token = oauth.Token(request_token['oauth_token'],
                    request_token['oauth_token_secret'])
token.set_verifier(oauth_verifier)
client = oauth.Client(consumer, token)

resp, content = client.request(access_token_url, "POST")
if resp['status'] != '200':
    raise Exception("Invalid response %s." % resp['status'])
access_token = dict(urlparse.parse_qsl(content))
    
print "Access Token:"
print "    - oauth_token        = %s" % access_token['oauth_token']
print "    - oauth_token_secret = %s" % access_token['oauth_token_secret']
print
print "You may now access protected resources using the access tokens above"
print


