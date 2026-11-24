// Get from OAI-PMH, write to local file for testing:
service_domain = "repository.tugraz.at";
service_id = "https://repository.tugraz.at";
service_name = "Repository TU Graz";
XML_FILE = FLUX_DIR + "repositoryTUGraz-metafacture.xml";

"https://repository.tugraz.at/oai2d"
| open-oaipmh(metadataPrefix="lom")
| as-lines
// Clean up invalid xml sign.
| match(pattern="&#0*(?:[0-8]|1[124-9]|2\\d|3[01]|5529[6-9]|55[3-9]\\d{2}|56\\d{3}|57[0-2]\\d{2}|573[0-3]\\d|5734[0-3]|6553[45])", replacement=" ")
| write(XML_FILE);

// Use local file for transformation (un-comment lines above to re-fetch data):

XML_FILE
| open-file
| decode-xml
| handle-generic-xml(emitNamespace="true")
| fix(FLUX_DIR + "repositoryTUGraz.fix", *)
| encode-json
| validate-json(output_schema, writeValid=metadata_valid, writeInvalid=metadata_invalid)
| oersi.OersiWriter(backend_api, user=backend_user, pass=backend_pass, log=metadata_responses)
;