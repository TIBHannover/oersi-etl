// Get from OAI-PMH, write to local file for testing:
service_domain = "phaidra.kphvie.ac.at";
service_id = "https://phaidra.kphvie.ac.at/";
service_name = "Phaidra KPH Wien/Niederösterreich";
XML_FILE = FLUX_DIR + "phaidraFHKaernten-metafacture.xml";

// First step collects hcrt for map used in step 2
"https://phaidra.kphvie.ac.at/api/oai"
| open-oaipmh(metadataPrefix="lom", setSpec="oer")
| decode-xml
| handle-generic-xml(emitNamespace="true")
| fix(FLUX_DIR + "../../sharedFixes/phaidraLomResourceTypes.fix")
| encode-csv(noquotes="true",separator="\t")
| write(FLUX_DIR + "lomResourceTypes.tsv")
;

// Transfomration of the openaire data since the lom data is not complete.
"https://phaidra.kphvie.ac.at/api/oai"
| open-oaipmh(metadataPrefix="oai_openaire", setSpec="oer")
| as-lines
| write(XML_FILE);

// Use local file for transformation (un-comment lines above to re-fetch data):

XML_FILE
| open-file
| decode-xml
| handle-generic-xml(emitNamespace="true")
| fix(FLUX_DIR + "phaidraKPHWien.fix",*)
| encode-json
| validate-json(output_schema, writeValid=metadata_valid, writeInvalid=metadata_invalid)
| oersi.ErrorCatcher(file_errors)
| oersi.OersiWriter(backend_api, user=backend_user, pass=backend_pass, log=metadata_responses)
;