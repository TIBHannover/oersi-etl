"https://www.tib.eu/oai/public/repository/av-portal"
| open-oaipmh(metadataPrefix="rdf_jsonld",dateFrom="2020-01-01",dateUntil="2020-03-31")
| decode-xml
| handle-generic-xml
// todo: first fix should only map the json data in field "metadata.value" but not as a string
| org.metafacture.metamorph.Metafix("
map(_else)
")
| encode-json
| decode-json
| fix(FLUX_DIR + "TIB.fix")
| encode-json
| oersi.FieldMerger
//| oersi.JsonValidator("https://dini-ag-kim.github.io/lrmi-profile/draft/schemas/schema.json")
| object-tee | {
    write(FLUX_DIR + "TIB-metadata.json", header="[\n", footer="\n]", separator=",\n")
  };