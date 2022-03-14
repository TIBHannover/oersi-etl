service_domain = "https://av.tib.eu/";
service_id = "https://oerworldmap.org/resource/urn:uuid:10c5092a-152d-4cc9-a823-e2deff43128e";
service_name = "TIB AV-Portal";


"https://getinfo.tib.eu/oai/intern/repository/tib"
| open-oaipmh(metadataPrefix="datacite", setSpec="collection~kmo-av_solr~documentFormat:el")
| decode-xml
| handle-generic-xml(emitNamespace="true")
| fix(FLUX_DIR + "tibav.fix", *)
| encode-json(prettyPrinting="true")
| oersi.JsonValidator(output_schema, writeValid=metadata_valid, writeInvalid=metadata_invalid)
| oersi.OersiWriter(backend_api, user=backend_user, pass=backend_pass, log=metadata_responses)
;