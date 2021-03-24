service_domain = "https://av.tib.eu/";
service_id = "https://oerworldmap.org/resource/urn:uuid:10c5092a-152d-4cc9-a823-e2deff43128e";
service_name = "TIB AV-Portal";

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
| fix(FLUX_DIR + "tibav.fix", *)
| encode-json(prettyPrinting="true")
| write(FLUX_DIR + "tibav-data-datacite.json", header="[\n", footer="\n]", separator=",\n")
;