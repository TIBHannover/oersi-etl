service_domain = "langsci-press.org/index";
service_id = "https://langsci-press.org/";
service_name = "langSci Press";


// Use as lookup file for missing metadata
"https://raw.githubusercontent.com/langsci/opendata/master/catalog.csv"
| open-http(header=user_agent_header, accept="application/csv")
| as-lines
| write(FLUX_DIR + "catalog.csv")
;


// Harvest oai
"https://langsci-press.org/oai"
| open-oaipmh(metadataPrefix="oai_dc")
| decode-xml
| handle-generic-xml(emitNamespace="true")
| fix(FLUX_DIR + "langSciPress_oai.fix",*)
| encode-json
| validate-json(output_schema, writeValid=metadata_valid, writeInvalid=metadata_invalid)
| oersi.OersiWriter(backend_api, user=backend_user, pass=backend_pass, log=metadata_responses)
;
