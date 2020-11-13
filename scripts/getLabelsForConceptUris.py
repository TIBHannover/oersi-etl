import requests
import json

filepath = '../data/maps/edusharing-subject-mapping.tsv'

input = open(filepath).readlines()

for row in input:
    if "https" in row:
        uri = row.split('\t')[1].rstrip()
        label = requests.get(uri, headers={"accept":"application/json"}).json()['prefLabel']['de'].encode('utf-8').strip()
        with open('../data/maps/subject-labels.tsv', 'a') as f:
            f.write(uri + "\t" + label + "\n")