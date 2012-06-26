#!/usr/bin/python
from OAuthApiTest import OAuthApiTest 

apiTest = OAuthApiTest("OAuth Provider Token/Consumer Details Tests")
    
apiTest.doGetTest("Test %d: List All Provider Tokens ", "https://%s/cedexis_ws/oauth/provider.json")
apiTest.doGetTest("Test %d: List All Consumer Details", "https://%s/cedexis_ws/oauth/consumer.json")

