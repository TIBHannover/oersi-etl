// Get from OAI-PMH, write to local file for testing:
service_domain = "canal-u.tv";
service_id = "https://www.canal-u.tv";
service_name = "CANAL U";

"https://www.canal-u.tv/oai/"
| open-oaipmh(metadataPrefix="oai_dc",dateFrom="2025-04-01")
| decode-xml
| handle-generic-xml(emitNamespace="true")
| fix(FLUX_DIR + "canalU.fix", *)
| encode-json
| validate-json(output_schema, writeValid=metadata_valid, writeInvalid=metadata_invalid)
| oersi.OersiWriter(backend_api, user=backend_user, pass=backend_pass, log=metadata_responses)
;