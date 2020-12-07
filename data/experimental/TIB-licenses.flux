"https://www.tib.eu/oai/public/repository/av-portal"
| open-oaipmh(metadataPrefix="rdf_jsonld",dateFrom="2020-03-01",dateUntil="2020-03-31")
| decode-xml
| handle-generic-xml
| fix("map(metadata.value)")
| literal-to-object
| decode-json
| fix("map('@graph[].1.license','license')")
| literal-to-object
| write(FLUX_DIR + "TIB-licenses.txt");