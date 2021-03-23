service_domain = "https://av.tib.eu/";
service_id = "https://oerworldmap.org/resource/urn:uuid:10c5092a-152d-4cc9-a823-e2deff43128e";
service_name = "TIB AV-Portal";

"https://getinfo.tib.eu/oai/intern/repository/tib"
| open-oaipmh(metadataPrefix="datacite", dateFrom="2020-03-02", dateUntil="2020-03-02", setSpec="kmo-av")
| log-object("OAI-IMPUT HHIEEEEEEEEEEER:")
// html-Decoder
| decode-xml
| handle-generic-xml
| fix(FLUX_DIR + "all.fix", *)
| encode-json
| oersi.FieldMerger
| oersi.JsonValidator(output_schema, writeValid=metadata_valid, writeInvalid=metadata_invalid)
//| oersi.OersiWriter(backend_api, user=backend_user, pass=backend_pass, log=metadata_responses)
| print
;