// Get from OAI-PMH, write to local file for testing:
service_domain = "repository.tugraz.at";
service_id = "https://repository.tugraz.at";
service_name = "Repository TU Graz";
XML_FILE = FLUX_DIR + "repositoryTUGraz-metafacture.xml";

"https://repository.tugraz.at/oai2d"
| open-oaipmh(metadataPrefix="lom")
| as-lines
| write(XML_FILE);

// Use local file for transformation (un-comment lines above to re-fetch data):

XML_FILE
| open-file
| decode-xml
| handle-generic-xml(emitNamespace="true")
| fix(FLUX_DIR + "repositoryTUGraz.fix", *)
| encode-json
| oersi.JsonValidator(output_schema, writeValid=metadata_valid, writeInvalid=metadata_invalid)
| oersi.OersiWriter(backend_api, user=backend_user, pass=backend_pass, log=metadata_responses)
;