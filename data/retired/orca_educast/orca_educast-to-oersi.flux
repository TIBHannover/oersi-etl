service_domain = "https://www.orca.nrw";
service_id = "https://oerworldmap.org/resource/urn:uuid:31c24f26-1a96-4664-8d6d-71fdddb8b1f5";
service_name = "ORCA.nrw";
XML_FILE = FLUX_DIR + "orca_educast-metafacture.xml";

"https://api.orca.educast.cloud/oaipmh/default" //Prod-System: https://dist.orca.educast.cloud/oaipmh/default
| open-oaipmh(metadataPrefix="matterhorn-inlined", setSpec="oersi")
| as-lines
| write(XML_FILE)
;

XML_FILE
| open-file
| decode-xml
| handle-generic-xml(emitNamespace="true")
| fix(FLUX_DIR + "orca_educast.fix", *)
| encode-json
| validate-json(output_schema, writeValid=metadata_valid, writeInvalid=metadata_invalid)
| oersi.OersiWriter(backend_api, user=backend_user, pass=backend_pass, log=metadata_responses)
;

