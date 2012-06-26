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

def encode_multipart_formdata(fields, files):
    """
    fields is a sequence of (name, value) elements for regular form fields.
    files is a sequence of (name, filename, value) elements for data to be 
    uploaded as files
    Return (content_type, body) ready for httplib.HTTP instance
    """
    BOUNDARY = mimetools.choose_boundary()
    CRLF = '\r\n'
    L = []
    for (key, value) in fields:
        L.append('--' + BOUNDARY)
        L.append('Content-Disposition: form-data; name="%s"' % key)
        L.append('')
        L.append(value)
    for (key, filename, value) in files:
        L.append('--' + BOUNDARY)
        L.append('Content-Disposition: form-data; name="%s"; filename="%s"' % (key, filename))
        L.append('Content-Type: %s' % get_content_type(filename))
        L.append('')
        L.append(value)
    L.append('--' + BOUNDARY + '--')
    L.append('')
    body = CRLF.join(L)
    content_type = 'multipart/form-data; boundary=%s' % BOUNDARY
    return content_type, body

def get_content_type(filename):
    return mimetypes.guess_type(filename)[0] or 'application/octet-stream'

class OAuthApiTest:

	def __init__(self, testsTitle):
		if len(sys.argv) < 4 or len(sys.argv) == 5:
			print_usage()
			sys.exit(-1)

		self.portal = sys.argv[1]

		if self.portal.isspace() or len(self.portal) == 0:
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

			self.token = oauth.Token(sys.argv[4], sys.argv[5])
		else:
			self.token = None

		self.consumer = oauth.Consumer(sys.argv[2],
								  sys.argv[3])
		self.testNum = 0
		print_banner(testsTitle)
	
	def doGetTest(self, testTitle, url):
		self.testNum += 1
		url = url % self.portal
		print_banner(testTitle % self.testNum)
		client = oauth.Client(self.consumer, self.token)
		resp, content = client.request(url, "GET")
		if resp['status'] != '200':
			print("Invalid response %s." % resp['status'])
			return resp
		else:
			print url,":"
			io = StringIO(content)
			j = json.load(io)
			print json.dumps(j, sort_keys=True, indent=4)
			print
			return j

	def doPostTest(self, testTitle, url, arguments):
		self.testNum += 1
		url = url % self.portal
		print_banner(testTitle % self.testNum)
		body = urllib.urlencode(arguments, True)
		print body
		client = oauth.Client(self.consumer, self.token)
		resp, content = client.request(url, "POST", body=body, realm="CedexisRealm")
		if resp['status'] != '200':
			print("Invalid response %s." % resp['status'])
			return resp
		else:
			print url,":"
			io = StringIO(content)
			j = json.load(io)
			print json.dumps(j, sort_keys=True, indent=4)
			print
			return j

	def doMultipartFormPostTest(self, testTitle, url, fields, files):
		self.testNum += 1
		url = url % self.portal
		print_banner(testTitle % self.testNum)
		(content_type, body) = encode_multipart_formdata(fields, files)
		print body
		client = oauth.Client(self.consumer, self.token)
		resp, content = client.request(url, "POST", body=body, headers={"Content-Type":content_type}, realm="CedexisRealm")
		if resp['status'] != '200':
			print("Invalid response %s." % resp['status'])
			return resp
		else:
			print url,":"
			io = StringIO(content)
			j = json.load(io)
			print json.dumps(j, sort_keys=True, indent=4)
			print
			return j


	def doDeleteTest(self, testTitle, url):
		self.testNum += 1
		url = url % self.portal
		print_banner(testTitle % self.testNum)
		client = oauth.Client(self.consumer, self.token)
		resp, content = client.request(url, "DELETE", realm="CedexisRealm")
		if resp['status'] != '200':
			print("Invalid response %s." % resp['status'])
			return resp
		else:
			print url,":"
			io = StringIO(content)
			j = json.load(io)
			print json.dumps(j, sort_keys=True, indent=4)
			print
			return j




