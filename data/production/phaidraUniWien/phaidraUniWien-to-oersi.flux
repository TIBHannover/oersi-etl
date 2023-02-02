// Get from OAI-PMH, write to local file for testing:
service_domain = "phaidra.univie.ac.at";
service_id = "https://phaidra.univie.ac.at/";
service_name = "Phaidra Uni Wien";
XML_FILE = FLUX_DIR + "phaidraUniWien-metafacture.xml";

"https://services.phaidra.univie.ac.at/api/oai"
| open-oaipmh(metadataPrefix="oai_openaire", setSpec="oer")
| as-lines
| write(XML_FILE);

// Use local file for transformation (un-comment lines above to re-fetch data):

XML_FILE
| open-file
| decode-xml
| handle-generic-xml(emitNamespace="true")
| fix(FLUX_DIR + "phaidraUniWien.fix",*)
| encode-json
| oersi.JsonValidator(output_schema, writeValid=metadata_valid, writeInvalid=metadata_invalid)
| oersi.ErrorCatcher(file_errors)
| oersi.OersiWriter(backend_api, user=backend_user, pass=backend_pass, log=metadata_responses)
;