import requests

panels = requests.get("https://panelapp.genomicsengland.co.uk/api/v1/panels").json()
for p in panels['results']:
    print "\t".join([str(p['id']), p['version'], p['name'], ";".join(p['relevant_disorders'])])

