#!/usr/bin/python
from OAuthApiTest import OAuthApiTest 

apiTest = OAuthApiTest("OAuth Admin Tests")

j = apiTest.doGetTest("Test %d: List All Providers ", "https://%s/cedexis_ws/provider.json")

valid_id = j['providers'][-1]['id']
apiTest.doGetTest("Test %d: Get the first Provider ", "https://%s" + "/cedexis_ws/provider/%s.json" % valid_id)

# create a new provider
# method must be POST.  Because the client uses the Authorization header
# if there is a request body, specify the correct realm for authorization
# cedexis web service realm is 'CedexisRealm'
provider = {
        'archetypeId':               0,
        'displayName':               "Web Service Provider",
        'nickName':                  'ws_created',
        'radarConfig.enabled':       True,
        'radarConfig.usePublicData': False,
        'radarConfig.rttUrl'       : 'http://www.origin.com/cdx10b.js',
        'radarConfig.primeUrl'     : 'http://www.origin.com/cdx10b.js',
        'pulseConfig.enabled':       True,
        'pulseConfig.liveUrl':       'http://www.origin.com/live.html',
        'pulseConfig.liveRateSeconds': 300,
        'fusionConfig.enabled':      False,
}
j = apiTest.doPostTest("Test %d: Create New Provider", "https://%s/cedexis_ws/provider.json", provider)

# modify the previous provider, then update it.  Extract id from prior response
# use PUT request.  specify _method=PUT to work around bugs with that method
provider['radarConfig.weight'] = 3
provider['radarConfig.xlUrl'] = 'http://www.origin.com/blah.html'
provider['pulseConfig.loadUrl'] = 'http://www.origin.com/load.txt'
provider['_method'] = "PUT"
j = apiTest.doPostTest("Test %d: Modify Provider", "https://%s" + "/cedexis_ws/provider/%s.json" % j['provider']['id'], provider)

# DELETE the provider we created and then edited.  Extract id from prior resp
apiTest.doDeleteTest("Test %d: Delete Provider", "https://%s" + "/cedexis_ws/provider/%s.json" % j['provider']['id'])

