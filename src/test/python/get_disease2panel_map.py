import requests


if __name__ == '__main__':

    more = True
    count = 0
    disease2panel = {}
    i = 1
    while more:
        panels_page = requests.get("https://panelapp.genomicsengland.co.uk/api/v1/panels?page={i}".format(i=i)).json()
        total_count = panels_page['count']
        i += 1
        for p in panels_page['results']:
            panel_code = "{id}-{version}".format(id=p["id"], version=p["version"])
            for disorder in p["relevant_disorders"]:
                panels_list = disease2panel.get(disorder, [])
                panels_list.append(panel_code)
                disease2panel[disorder] = panels_list
            count += 1
        if count >= total_count:
            more = False

    fd = open("./disease2panel.tsv", "w")
    for disease in disease2panel:
        for panel_id in disease2panel[disease]:
            print("{disease}\t{panel_id}".format(disease=disease, panel_id=panel_id))
            fd.write("{disease}\t{panel_id}\n".format(disease=disease, panel_id=panel_id))
    fd.close()
