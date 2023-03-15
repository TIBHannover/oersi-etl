// Get from OAI-PMH, write to local file for testing:
service_domain = "https://duepublico2.uni-due.de";
service_id = "https://oerworldmap.org/resource/urn:uuid:c9d74da8-bf88-4f3e-b601-f2a3f6d40330";
service_name = "DuEPublico";
XML_FILE = FLUX_DIR + "duepub-metafacture.xml";

"https://duepublico2.uni-due.de/oer/oai"
| open-oaipmh(metadataPrefix="mods")
| as-lines
| write(XML_FILE);

// Use local file for transformation (un-comment lines above to re-fetch data):

XML_FILE
| open-file
| decode-xml
| handle-generic-xml(emitNamespace="true")
| fix(FLUX_DIR + "duepublico.fix", *)
| encode-json
| validate-json(output_schema, writeValid=metadata_valid, writeInvalid=metadata_invalid)
| oersi.OersiWriter(backend_api, user=backend_user, pass=backend_pass, log=metadata_responses)
;