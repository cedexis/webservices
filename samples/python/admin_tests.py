#!/usr/bin/python
from OAuthApiTest import OAuthApiTest 

apiTest = OAuthApiTest("OAuth Admin Tests")

apiTest.doGetTest("Test %d: List All Tax Rates ", "https://%s/cedexis_ws/admin/taxrate.json")
apiTest.doGetTest("Test %d: List All Languages ", "https://%s/cedexis_ws/admin/language.json")
apiTest.doGetTest("Test %d: List All Currency ", "https://%s/cedexis_ws/admin/currency.json")
apiTest.doGetTest("Test %d: List All Countries ", "https://%s/cedexis_ws/admin/country.json")
apiTest.doGetTest("Test %d: List All Countries ", "https://%s/cedexis_ws/admin/channel.json")
j = apiTest.doGetTest("Test %d: List All Accounts ", "https://%s/cedexis_ws/admin/account.json")
id = j['accounts'][0]['id']
apiTest.doGetTest("Test %d: List All Accounts ", "https://%s" + "/cedexis_ws/admin/account/%d.json" % id)
newAccount = { 	'channelId': 3,
				'name': 'something9',
				'language': 'en',
				'billingCurrency':'USD',
				'taxRate':2,
				'country':2,
				'emails':'somethin9@yada.com'}

apiTest.doPostTest("Test %d: Create New Account ", "https://%s/cedexis_ws/admin/account.json", newAccount)

