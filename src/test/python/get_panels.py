import requests

more = True
count = 0
i = 1
while more:
    panels_page = requests.get("https://panelapp.genomicsengland.co.uk/api/v1/panels?page={i}".format(i=i)).json()
    total_count = panels_page['count']
    i += 1
    for p in panels_page['results']:
        count += 1
        print "\t".join([str(p['id']), p['version'], p['name'], ";".join(p['relevant_disorders'])])
    if count >= total_count:
        more = False

print "Written {} panels".format(count)

