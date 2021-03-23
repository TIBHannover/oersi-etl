// out-comment first workflow to use local data

"https://www.tib.eu/oai/public/repository/av-portal"
| open-oaipmh(metadataPrefix="marc_xml", dateFrom="2020-03-06", dateUntil="2020-03-16")
| as-lines
| write(FLUX_DIR + "tibav-data-marc.xml")
;

FLUX_DIR + "tibav-data-marc.xml"
| open-file
| decode-xml
| handle-marcxml
| fix("
do entity('subject[]', flushWith: 'record')
 map('65014.a|084  .a')
end
", *)
| encode-json(prettyPrinting="true")
| write(FLUX_DIR + "tibav-data-marc.json", header="[\n", footer="\n]", separator=",\n")
;