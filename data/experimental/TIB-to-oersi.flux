"https://www.tib.eu/oai/public/repository/av-portal"
| open-oaipmh(metadataPrefix="rdf_jsonld",dateFrom="2020-05-01",dateUntil="2020-05-14")
| decode-json
| fix(FLUX_DIR + "TIB.fix")
| encode-json
| oersi.FieldMerger
//| oersi.JsonValidator("https://dini-ag-kim.github.io/lrmi-profile/draft/schemas/schema.json")
| object-tee | {
    write(FLUX_DIR + "TIB-metadata.json", header="[\n", footer="\n]", separator=",\n")
  };