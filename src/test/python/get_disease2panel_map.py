import requests


if __name__ == '__main__':
    fd = open("/tmp/disease2panel.tsv", "w")
    more = True
    count = 0
    disease2panel_dict = {}
    i = 1
    while more:
        panels_page = requests.get("https://panelapp.genomicsengland.co.uk/api/v1/panels?page={i}".format(i=i)).json()
        total_count = panels_page['count']
        i += 1
        for p in panels_page['results']:
            if len(p["relevant_disorders"]) > 1:
                a = 1

            for disorder in p["relevant_disorders"]:
                if disorder in disease2panel_dict:
                    disease2panel_dict[disorder].add("{id}-{version}".format(id=p["id"], version=p["version"]))
                else:
                    disease2panel_dict[disorder] = {"{id}-{version}".format(id=p["id"], version=p["version"])}
                #     a = 1
                # disease2panel_dict.setdefault(disorder, set([])) \
                #     .add("{id}-{version}".format(id=p["id"], version=p["version"]))
            count += 1
        if count >= total_count:
            more = False

    for disease in disease2panel_dict:
        for panel_id in disease2panel_dict[disease]:
            print("{disease}\t{panel_id}".format(disease=disease, panel_id=panel_id))
            fd.write("{disease}\t{panel_id}\n".format(disease=disease, panel_id=panel_id))

    fd.close()
    print("Number of panels: {npanels}".format(npanels=count))