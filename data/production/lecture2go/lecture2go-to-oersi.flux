service_domain = "lecture2go.uni-hamburg.de/";
service_id = "https://lecture2go.uni-hamburg.de/";
service_name = "lecture2go";

// Use for testing:
// "https://getinfo.tib.eu/oai/intern/repository/tib?verb=ListRecords&metadataPrefix=datacite&set=collection~kmo-av_solr~documentFormat:el"
//| open-http(header=user_agent_header, accept="application/xml")

"https://lecture2go.uni-hamburg.de/o/oai"
| open-oaipmh(metadataPrefix="oai_datacite")
| decode-xml
| handle-generic-xml(emitNamespace="true")
| fix(FLUX_DIR + "lecture2go.fix", *)
| encode-json
| validate-json(output_schema, writeValid=metadata_valid, writeInvalid=metadata_invalid)
| oersi.ErrorCatcher(file_errors)
| oersi.OersiWriter(backend_api, user=backend_user, pass=backend_pass, log=metadata_responses)
;