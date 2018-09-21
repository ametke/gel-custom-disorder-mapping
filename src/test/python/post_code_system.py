import requests
import os.path
import json

url_base = "http://ontoserver.dataproducts.nhs.uk/fhir"
id = "panelapp"
data = json.load(open('../panelapp_panels.json'))
headers = {
    'content-type': "application/json",
    'cache-control': "no-cache"
}
response = requests.put(url=os.path.join(url_base, "CodeSystem/panelapp"), data=data, headers=headers)