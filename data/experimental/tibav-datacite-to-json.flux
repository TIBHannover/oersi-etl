// out-comment first workflow to use local data

"https://getinfo.tib.eu/oai/intern/repository/tib"
| open-oaipmh(metadataPrefix="datacite", dateFrom="2020-03-06", dateUntil="2020-03-16", setSpec="kmo-av")
| as-lines
| write(FLUX_DIR + "tibav-data-datacite.xml")
;

FLUX_DIR + "tibav-data-datacite.xml"
| open-file
| decode-xml
| handle-generic-xml
| fix("
do entity('subject[]', flushWith: 'record')
 map('metadata.resources.resource.subjects.subject.value')
end
", *)
| encode-json(prettyPrinting="true")
| write(FLUX_DIR + "tibav-data-datacite.json", header="[\n", footer="\n]", separator=",\n")
;